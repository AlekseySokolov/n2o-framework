import React from 'react'
import { pure } from 'recompose'
import pick from 'lodash/pick'
import get from 'lodash/get'
import PropTypes from 'prop-types'
import classNames from 'classnames'

/**
 * Компонент создания строки в таблице
 * @param props
 * @constructor
 */
function AdvancedTableRow(props) {
    const {
        className,
        isRowActive,
        setRef,
        children,
        model,
        rowClass,
        handleRowClick,
        handleRowClickFocus,
    } = props

    const classes = classNames(className, 'n2o-table-row n2o-advanced-table-row', {
        'table-active': isRowActive,
        [rowClass]: rowClass,
    })
    const newProps = {
        ...pick(props, ['className', 'data-row-key', 'style']),
        ref: el => setRef && setRef(el, model.id),
        tabIndex: 0,
        key: model.id,
        className: classes,
        onClick: handleRowClick,
        onFocus: handleRowClickFocus,
    }

    const resolvedChildren = children.filter(child => get(child, 'props.column.visible') !== false &&
        (get(child, 'key') || get(child, 'props.column.component')))

    return React.createElement('tr', newProps, [...resolvedChildren])
}

AdvancedTableRow.propTypes = {
    handleRowClickFocus: PropTypes.func,
    handleRowClick: PropTypes.func,
    rowClass: PropTypes.string,
    className: PropTypes.string,
    isRowActive: PropTypes.bool,
    setRef: PropTypes.func,
    children: PropTypes.array,
    model: PropTypes.object,
}

export default pure(AdvancedTableRow)
