import { exportFormName } from '../../../components/widgets/Table/ExportModal'
import {
    makeWidgetPageSelector,
    makeWidgetSizeSelector,
    makeWidgetCountSelector,
} from '../../../ducks/widgets/selectors'
import { destroyOverlay } from '../../../ducks/overlays/store'
import { ModelPrefix } from '../../../core/datasource/const'
import { getModelByPrefixAndNameSelector } from '../../../ducks/models/selectors'

/**
 * Функция для кодирования query
 * @param data
 * @returns {string}
 */
export function encodeQueryData(data) {
    const ret = Object
        .entries(data)
        .reduce((acc, [key, value]) => [...acc, `${encodeURIComponent(key)}=${encodeURIComponent(value)}`], [])
        .join('&')

    return `/export?${ret}`
}

/**
 * Экшен экспорта таблицы
 * @param dispatch
 * @param state
 * @param widgetId
 */
export default function resolveExportTable({ dispatch, state, widgetId }) {
    const values = getModelByPrefixAndNameSelector(ModelPrefix.active, exportFormName, {})(state)
    const page = values.size === 'all' ? 1 : makeWidgetPageSelector(widgetId)(state)
    const size = makeWidgetSizeSelector(widgetId)(state)
    const count = values.size === 'all' ? makeWidgetCountSelector(widgetId)(state) : size

    window.open(
        encodeQueryData({
            widgetId,
            contentType: values.type,
            code: values.code,
            page,
            count,
        }),
        '_blank',
    )
    dispatch(destroyOverlay())
}
