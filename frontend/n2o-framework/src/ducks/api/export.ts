import { createAction } from '@reduxjs/toolkit'
import { select, takeEvery, cancel } from 'redux-saga/effects'

// @ts-ignore ignore import error from js file
import { dataProviderResolver } from '../../core/dataProviderResolver'
import { dataSourceByIdSelector } from '../datasource/selectors'
import { getTableColumns } from '../table/selectors'
import { Columns } from '../columns/Columns'
import { Action } from '../Action'
import { getModelSelector } from '../models/selectors'
import { ModelPrefix } from '../../core/datasource/const'
import { DataSourceState } from '../datasource/DataSource'
import { State } from '../State'

import { UTILS_PREFIX } from './constants'
import { EffectWrapper } from './utils/effectWrapper'
import { escapeUrl } from './utils/escapeUrl'

const ATTRIBUTES_ERROR = 'Ошибка экспорта, payload содержит не все параметры'
const PARAMS_ERROR = 'Ошибка экспорта, не передан формат или кодировка'
const SHOW = 'show'
const PARAM_KEY = 'id'

export type Payload = {
    exportDatasource: string
    configDatasource: string
    baseURL: string
    widgetId: string
    allLimit: number
}

export const creator = createAction(
    `${UTILS_PREFIX}export`,
    (payload: Payload, meta: object) => ({
        payload,
        meta,
    }),
)

function getShowedColumns(columns: Columns): string[] {
    const ids = Object.keys(columns) || []

    return ids.filter(id => columns[id].visible && columns[id].visibleState)
}

function createExportUrl(
    resolvedURL: string,
    baseURL: string,
    format: string,
    charset: string,
    showed: string[],
) {
    const { pathname } = window.location

    const path = pathname.slice(0, -1)
    const exportURL = `${path}${baseURL}?format=${format}&charset=${charset}&url=`

    if (!showed.length) {
        return `${exportURL}${escapeUrl(resolvedURL)}`
    }

    let url = resolvedURL

    for (const show of showed) {
        url += `&${SHOW}=${show}`
    }

    return `${exportURL}${escapeUrl(url)}`
}

interface ExportConfig {
    format: {
        [PARAM_KEY]: string
    }
    charset: {
        [PARAM_KEY]: string
    }
    type: {
        [PARAM_KEY]: 'all' | 'page'
        name: string
    }
}

export function* effect({ payload }: Action<string, Payload>) {
    const { exportDatasource, configDatasource, baseURL, widgetId, allLimit = 1000 } = payload

    if (!exportDatasource || !configDatasource || !baseURL || !widgetId) {
        // eslint-disable-next-line no-console
        console.error(ATTRIBUTES_ERROR)

        yield cancel()
    }

    const modelLink = `models.${ModelPrefix.active}.${configDatasource}`
    const model: ExportConfig = yield select(getModelSelector(modelLink))
    const { type, format: modelFormat, charset: modelCharset } = model

    const format = modelFormat[PARAM_KEY]
    const charset = modelCharset[PARAM_KEY]

    if (!format || !charset) {
        // eslint-disable-next-line no-console
        console.error(PARAMS_ERROR)

        yield cancel()
    }

    const state: State = yield select()

    const dataSource: DataSourceState = yield select(dataSourceByIdSelector(exportDatasource))
    const { provider, paging, sorting = {} } = dataSource
    const { url: resolvedURL } = dataProviderResolver(state, provider, {
        size: type[PARAM_KEY] === 'page' ? paging.size : allLimit,
        page: type[PARAM_KEY] === 'page' ? paging.page : 1,
        sorting,
    })

    const columns: Columns = yield select(getTableColumns(widgetId))

    const showed = getShowedColumns(columns)

    const exportURL = createExportUrl(resolvedURL, baseURL, format, charset, showed)

    window.open(exportURL, '_blank')
}

// @ts-ignore проблема с типизацией saga
export const sagas = [takeEvery(creator.type, EffectWrapper(effect))]
