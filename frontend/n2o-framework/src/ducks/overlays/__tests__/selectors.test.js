import { overlaysSelector, makeOverlayByIndex } from '../selectors'

const state = {
    overlays: [
        {
            some: 'overlay',
        },
    ],
}

describe('Проверка селекторов overlays', () => {
    it('overlaysSelector должен вернуть overlay', () => {
        expect(overlaysSelector(state)).toEqual(state.overlays)
    })
    it('makeOverlaysByName должен вернуть оверлей по индексу', () => {
        expect(makeOverlayByIndex(0)(state)).toEqual(state.overlays[0])
    })
})
