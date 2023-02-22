import {
    modelsSelector,
    getModelByPrefixAndNameSelector,
    getGlobalFieldByPath,
    getModelsByDependency,
    makeModelsByPrefixSelector,
} from '../selectors'

const state = {
    models: {
        resolve: {
            widgetId: {
                some: 'value',
            },
        },
        filter: {
            widgetId: {
                other: 'value',
            },
        },
        edit: {
            testWidgetId: {
                value: 'value',
            },
        },
    },
}

describe('Проверка селекторов models', () => {
    it('modelsSelector должен вернуть models', () => {
        expect(modelsSelector(state)).toEqual(state.models)
    })
    it('makeModelsByPrefixSelector должен вернуть модель по префиксу', () => {
        expect(makeModelsByPrefixSelector('edit')(state)).toEqual(
            state.models.edit,
        )
    })
    it('makeGetModelByPrefixSelector должен вернуть модель по префиксу и ключу', () => {
        expect(getModelByPrefixAndNameSelector('edit', 'testWidgetId')(state)).toEqual(
            state.models.edit.testWidgetId,
        )
    })
    it('getModelSelector должен вернуть модель по ссылке', () => {
        expect(getGlobalFieldByPath('models.resolve.widgetId')(state)).toEqual(
            state.models.resolve.widgetId,
        )
    })
    it('getModelsByDependency должен вернуть модель по ссылке', () => {
        expect(
            getModelsByDependency([{ on: 'models.edit.testWidgetId' }])(state),
        ).toEqual([
            {
                config: {
                    on: 'models.edit.testWidgetId',
                },
                model: {
                    value: 'value',
                },
            },
        ])
    })
})
