import React, { useContext } from 'react'
import PropTypes from 'prop-types'
import isEmpty from 'lodash/isEmpty'

import { FactoryContext } from '../../../core/factory/context'
import { SNIPPETS } from '../../../core/factory/factoryLevels'

/**
 * Компонент табличной пейджинации. По `widgetId` автоматически определяет все свойства для `Paging`
 * @reactProps {string} widgetId - уникальный индефикатор виджета
 * @reactProps {number} count
 * @reactProps {number} size
 * @reactProps {number} activePage
 * @reactProps {function} onChangePage
 */
export const N2OPagination = (props) => {
    const {
        datasource,
        setPage,
        showCount: propShowCount,
        loading,
        hasNext,
        count,
        visible = true,
    } = props
    const { getComponent } = useContext(FactoryContext)
    const showCount = propShowCount || !isEmpty(datasource)
    const calculatedHasNext = count ? hasNext : (!loading && hasNext)

    if (!visible) {
        return null
    }

    const Pagination = getComponent('Pagination', SNIPPETS)

    return (
        <Pagination
            {...props}
            onSelect={setPage}
            showCount={showCount}
            hasNext={calculatedHasNext}
            loading={loading}
            showSinglePage={false}
        />
    )
}

N2OPagination.displayName = 'N2OPagination'

N2OPagination.propTypes = {
    count: PropTypes.number,
    size: PropTypes.number,
    activePage: PropTypes.number,
    setPage: PropTypes.func,
    datasource: PropTypes.array,
    layout: PropTypes.oneOf([
        'bordered',
        'flat',
        'separated',
        'flat-rounded',
        'bordered-rounded',
        'separated-rounded',
    ]),
    prev: PropTypes.bool,
    prevIcon: PropTypes.string,
    next: PropTypes.bool,
    nextIcon: PropTypes.string,
    last: PropTypes.bool,
    lastIcon: PropTypes.string,
    showCount: PropTypes.bool,
    className: PropTypes.string,
    style: PropTypes.object,
}
