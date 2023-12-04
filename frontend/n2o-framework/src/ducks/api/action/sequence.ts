import { createAction } from '@reduxjs/toolkit'
import { cancel } from 'redux-saga/effects'

import { Action, ErrorAction, Meta } from '../../Action'
import { ACTIONS_PREFIX } from '../constants'
import { waitOperation } from '../utils/waitOperation'
import { mergeMeta } from '../utils/mergeMeta'
import { failOperation } from '../Operation'

export type Payload = {
    actions: Action[]
}

export const creator = createAction(
    `${ACTIONS_PREFIX}sequence`,
    (payload: Payload, meta: Meta) => ({
        payload,
        meta,
    }),
)

export function* effect({ payload, meta }: ReturnType<typeof creator>) {
    const { actions } = payload
    const { target } = meta

    for (const action of actions) {
        const resultAction: Action | ErrorAction = yield waitOperation(mergeMeta(action, { target }))

        if (resultAction.type === failOperation.type) {
            yield cancel()
        }

        if (resultAction.error) {
            throw new Error(resultAction.error)
        }
    }
}
