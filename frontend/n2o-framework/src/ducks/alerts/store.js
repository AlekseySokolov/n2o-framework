// TODO: Дописать тесты которых не хватает, если таковые имеются
import { createSlice, createSelector } from '@reduxjs/toolkit'
import take from 'lodash/take'

import { ALLOWED_ALERTS_QUANTITY } from './constants'

export const initialState = {}

const setAlertsToStore = (state, action) => {
    const { key, alerts } = action.payload

    if (!state[key]) {
        state[key] = []
    }

    state[key].unshift(...alerts)
    state[key] = take(state[key], ALLOWED_ALERTS_QUANTITY)
}

const alertsSlice = createSlice({
    name: 'n2o/alerts',
    initialState,
    reducers: {

        ADD: {
            /**
             * @param {string} alertStoreKey
             * @param {AlertsStore.item} alert
             * @return {{payload: AlertsStore.addPayload}}
             */
            prepare(alertStoreKey, alert) {
                return ({
                    payload: { key: alertStoreKey, alerts: [alert] },
                })
            },

            /**
             * Добавление алерта в стор
             * @param {AlertsStore.state} state
             * @param {Object} action
             * @param {string} action.type
             * @param {AlertsStore.addPayload} action.payload
             */
            reducer: setAlertsToStore,
        },

        ADD_MULTI: {

            /**
             * @param {string} alertStoreKey
             * @param {AlertsStore.item[]} alerts
             * @return {{payload: AlertsStore.addMultiPayload}}
             */
            prepare(alertStoreKey, alerts) {
                return ({
                    payload: { key: alertStoreKey, alerts },
                })
            },

            /**
             * Добавление алертов в стор
             * @param {AlertsStore.state} state
             * @param {Object} action
             * @param {string} action.type
             * @param {AlertsStore.addMultiPayload} action.payload
             */
            reducer: setAlertsToStore,
        },

        REMOVE: {
            /**
             * @param {string} alertStoreKey
             * @param {string} alertId
             * @return {{payload: AlertsStore.removePayload}}
             */
            prepare(alertStoreKey, alertId) {
                return ({
                    payload: { key: alertStoreKey, id: alertId },
                })
            },

            /**
             * Удаление алерта по id из стора алертов по ключу(widgetId)
             * @param {AlertsStore.state} state
             * @param {Object} action
             * @param {string} action.type
             * @param {AlertsStore.removePayload} action.payload
             */
            reducer(state, action) {
                const { key, id } = action.payload

                if (!state[key] || !state[key].length) {
                    return
                }

                const filtered = state[key].filter(alert => alert.id !== id)

                if (filtered.length) {
                    state[key] = filtered
                } else {
                    delete state[key]
                }
            },
        },

        /**
         * Удаление алертов алертов по ключу(widgetId)
         * @param {AlertsStore.state} state
         * @param {Object} action
         * @param {string} action.type
         * @param {AlertsStore.removeAllPayload} action.payload
         */
        REMOVE_ALL(state, action) {
            if (!state[action.payload]) {
                return
            }

            delete state[action.payload]
        },

        STOP_REMOVING: {
            prepare(alertStoreKey, id) {
                return ({
                    payload: { alertStoreKey, id },
                })
            },
            reducer(state, action) {
                const { alertStoreKey, id } = action.payload

                state[alertStoreKey] = state[alertStoreKey].map((alert) => {
                    if (alert.id === id) {
                        return { ...alert, stopped: true }
                    }

                    return alert
                })
            },
        },
    },
})

// Selectors
/**
 * Селектор алертов
 * @param {Object} store
 * @return {AlertsStore.state}
 */
const alertsSelector = store => store.alerts

/**
 * Селектор айтемов по ключу(widgetId)
 * @param {string} key
 * @returns {AlertsStore.item[]}
 */
export const alertsByKeySelector = key => createSelector(
    alertsSelector,
    alertsStore => alertsStore[key] || [],
)

/**
 * Селектор айтема по ключу(widgetId) и id алерта
 * @param {string} key
 * @param {string} id
 * @returns {AlertsStore.item | null}
 */
export const alertByIdAndKeySelector = (key, id) => createSelector(
    alertsByKeySelector(key),
    alerts => (alerts.length ? alerts[id] : null),
)

// Actions
export const {
    ADD: addAlert,
    ADD_MULTI: addMultiAlerts,
    REMOVE: removeAlert,
    REMOVE_ALL: removeAllAlerts,
    STOP_REMOVING: stopRemoving,
} = alertsSlice.actions
export default alertsSlice.reducer
