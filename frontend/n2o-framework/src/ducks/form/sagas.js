import { takeEvery, put, select, debounce, delay } from 'redux-saga/effects'
import get from 'lodash/get'
import set from 'lodash/set'
import values from 'lodash/values'
import includes from 'lodash/includes'
import merge from 'lodash/merge'
import entries from 'lodash/entries'
import isEmpty from 'lodash/isEmpty'
import isObject from 'lodash/isObject'
import find from 'lodash/find'

import { widgetsSelector } from '../widgets/selectors'
import {
    setModel,
    copyModel,
    clearModel,
    updateModel,
    appendFieldToArray,
    removeFieldFromArray,
    copyFieldArray,
} from '../models/store'
import {
    getModelByPrefixAndNameSelector,
    modelsSelector,
} from '../models/selectors'
import { dataSourceByIdSelector } from '../datasource/selectors'
import evalExpression from '../../utils/evalExpression'
// import { setTabInvalid } from '../regions/store'
import { failValidate, startValidate, submit } from '../datasource/store'
import { ModelPrefix } from '../../core/datasource/const'
// import { generateFormFilterId } from '../../utils/generateFormFilterId'
import { ValidationsKey } from '../../core/validation/IValidation'
import { EffectWrapper } from '../api/utils/effectWrapper'

import { makeFormsFiltersByDatasourceSelector, makeFormsByDatasourceSelector } from './selectors'
import {
    setRequired,
    unsetRequired,
    handleBlur,
    handleTouch,
} from './store'

export function* copyAction({ payload }) {
    const { target, source, mode = 'replace', sourceMapper: expression } = payload
    const state = yield select(modelsSelector)
    let sourceModel = get(state, values(source).join('.'))
    const selectedTargetModel = yield select(getModelByPrefixAndNameSelector(target.prefix, target.key))
    const targetModel = selectedTargetModel || []
    let newModel

    const { field = null } = target
    const targetModelField = get(targetModel, field, [])

    const treePath = includes(target.field, '.')

    if (expression) {
        sourceModel = evalExpression(expression, sourceModel)
    }

    if (mode === 'merge') {
        if (!target.field) {
            newModel = { ...targetModel, ...sourceModel }
        } else if (isObject(sourceModel) || Array.isArray(sourceModel)) {
            newModel = {
                ...targetModel,
                [target.field]: {
                    ...targetModelField,
                    ...sourceModel,
                },
            }
        } else {
            newModel = {
                ...targetModel,
                [target.field]: sourceModel,
            }
        }
    } else if (mode === 'add') {
        if (!Array.isArray(sourceModel) || !Array.isArray(targetModelField)) {
            // eslint-disable-next-line no-console
            console.warn('Source or target is not an array!')
        }

        if (!Array.isArray(sourceModel)) {
            /* the key by which the data is copied to the target model */
            if (field) {
                newModel = { ...targetModel, [field]: targetModelField.concat(sourceModel) }
            } else {
                newModel = targetModel.concat(sourceModel)
            }
        } else {
            sourceModel = Object.values(sourceModel)

            newModel = target.field
                ? {
                    ...targetModel,
                    [target.field]: [...targetModelField, ...sourceModel],
                }
                : [...targetModelField, ...sourceModel]
        }
    } else if (treePath) {
        newModel = merge({}, targetModel)
        set(newModel, target.field, sourceModel)
    } else {
        newModel = target.field
            ? {
                ...targetModel,
                [target.field]: sourceModel,
            }
            : sourceModel
    }

    yield put(setModel(target.prefix, target.key, newModel))

    // костыль, тк я убрал резолв зависимостей на redux-form/INITIALIZE из-за беспорядочных вызовов,
    // которые приводили к зацикливанию при applyOnInit: true и переинициализации, зависимости перестали вызываться
    // после копирования
    for (const [field, value] of entries(newModel)) {
        if (get(targetModel, field) !== value) {
            yield put(updateModel(target.prefix, target.key, field, value))
        }
    }
}

// TODO: Починить сагу
/* it uses in tabs region */
// function* setFocus({ payload }) {
//     const { validation } = payload
//     const { form, fields, blurValidation } = validation
//
//     if (!blurValidation) {
//         /* set focus to first invalid field */
//         yield put(focus(form, Object.keys(fields)[0]))
//     }
// }

export function* clearForm({ payload }) {
    /*
    * FIXME: ХАК для быстрого фикса. Разобраться
    * если дёргать ресет формы разу после очистки модели, то форма сетает первый введёный в ней символ
    * поставил задержку, чтобы форма могла сначала принять в себя пустую модель, а потом уже ресетнуть всю мета инфу в себе
    */
    const { prefixes, key } = payload
    const formWidgets = yield select(makeFormsByDatasourceSelector(key))
    const widgetsWithFilter = yield select(makeFormsFiltersByDatasourceSelector(key))

    yield delay(50)

    for (const formWidget of formWidgets) {
        const modelPrefix = get(formWidget, ['form', 'modelPrefix'], ModelPrefix.active)

        if (includes(prefixes, modelPrefix)) {
            yield put(setModel(modelPrefix, key, {}))
        }
    }

    /* костыль для очистки фильтров виджета через clear action */
    for (const widgetFilter of widgetsWithFilter) {
        const { datasource = null } = widgetFilter

        if (datasource) {
            yield put(setModel(ModelPrefix.filter, datasource, {}))
        }
    }
}

/* TODO перенести в саги datasource
 * как вариант, чтобы не искать какая форма и событие вызывает автосейв можно сделать
 * autoSubmit: { action: ReduxAction, condition: expressionString(datasource, action) }
 */
export function* autoSubmit({ meta }) {
    const { key, field } = meta
    const datasource = yield select(dataSourceByIdSelector(key))

    if (isEmpty(datasource)) { return }

    const provider = (datasource.submit?.auto || datasource.submit?.autoSubmitOn)
        ? datasource.submit
        : datasource.fieldsSubmit[field]

    if (!isEmpty(provider)) {
        yield put(submit(key, provider))
    }
}

const validateFields = {}

export const formPluginSagas = [
    takeEvery(clearModel, clearForm),
    takeEvery(copyModel, EffectWrapper(copyAction)),
    takeEvery(failValidate, function* touchOnFailValidate({ payload, meta }) {
        if (!meta?.touched) { return }

        const { prefix, id, fields } = payload
        const keys = Object.keys(fields)

        yield put(handleTouch(prefix, id, keys))
    }),
    takeEvery([
        updateModel,
        handleBlur,
        setRequired,
        unsetRequired,
        appendFieldToArray,
        removeFieldFromArray,
        copyFieldArray,
    ], ({ meta }) => {
        const { key: datasource, field } = meta

        if (!validateFields[datasource]) {
            validateFields[datasource] = []
        }

        validateFields[datasource].push(field)
    }),
    debounce(200, [
        updateModel,
        handleBlur,
        setRequired,
        unsetRequired,
        appendFieldToArray,
        removeFieldFromArray,
        copyFieldArray,
    ], function* validateSaga({ meta }) {
        const { key: datasource } = meta
        const allWidgets = yield select(widgetsSelector)

        const widget = find(allWidgets, { datasource })

        if (isEmpty(widget)) {
            // eslint-disable-next-line no-console
            console.warn(`Не найден виджет для формы: ${datasource}`)

            return
        }

        const isFilter = !widget.form
        const prefix = isFilter ? ModelPrefix.filter : get(widget, ['form', 'modelPrefix'], ModelPrefix.active)
        const fields = validateFields[datasource]

        validateFields[datasource] = undefined

        if (!isEmpty(fields)) {
            yield put(
                startValidate(
                    datasource,
                    isFilter ? ValidationsKey.FilterValidations : ValidationsKey.Validations,
                    prefix,
                    fields,
                    { blurValidation: true },
                ),
            )
        }
    }),
    debounce(400, [
        updateModel,
        appendFieldToArray,
        removeFieldFromArray,
        copyFieldArray,
    ], autoSubmit),
]
