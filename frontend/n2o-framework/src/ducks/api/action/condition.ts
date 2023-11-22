import { createAction } from '@reduxjs/toolkit'
import isEmpty from 'lodash/isEmpty'
import { select } from 'redux-saga/effects'

import { Action, ErrorAction, Meta } from '../../Action'
import { ModelPrefix } from '../../../core/datasource/const'
import evalExpression from '../../../utils/evalExpression'
import { getModelByPrefixAndNameSelector } from '../../models/selectors'
import { ACTIONS_PREFIX } from '../constants'
import { waitOperation } from '../utils/waitOperation'
import { mergeMeta } from '../utils/mergeMeta'

export type Payload = {
    datasource: string
    model: ModelPrefix
    condition: string,
    success: Action,
    fail: Action
}

export const creator = createAction(
    `${ACTIONS_PREFIX}condition`,
    (payload: Payload, meta: Meta) => ({
        payload,
        meta,
    }),
)

export function* effect({ payload, meta }: ReturnType<typeof creator>) {
    const { datasource, model: modelPrefix, condition, success, fail } = payload
    const { target } = meta
    const model: object = yield select(getModelByPrefixAndNameSelector(modelPrefix, datasource))
    const action = evalExpression(condition, model) ? success : fail

    if (!isEmpty(action)) {
        const resultAction: Action | ErrorAction = yield waitOperation(mergeMeta(action, { target }))

        if (resultAction.error) {
            throw new Error(resultAction.error)
        }
    }
}
