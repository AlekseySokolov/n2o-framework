import React from 'react'
import pure from 'recompose/pure'
import PropTypes from 'prop-types'
import classNames from 'classnames'

/**
 * Компонент обертка Cell
 * @param children - вставляемый компонент
 * @param hasSpan - флаг возможности colSpan/rowSpan в этой колонке
 * @param record - модель строки
 * @param textWrap - флаг на запрет/разрешение переноса текста в cell (default = true)
 * @param wordWrap - динамичный флаг на запрет/разрешение переноса текста в cell (default = undefined)
 * @param needRender - флаг отрисовки cell
 * @returns {*}
 * @constructor
 */
function AdvancedTableCell({ children, hasSpan, record, textWrap, needRender, style }) {
    const { span } = record

    let colSpan = 1
    let rowSpan = 1

    if (hasSpan && span) {
        if (span.colSpan === 0 || span.rowSpan === 0) {
            return null
        }

        colSpan = span.colSpan
        rowSpan = span.rowSpan
    }

    const childrenWithProps = React.Children.map(children, (child) => {
        if (React.isValidElement(child)) {
            return React.cloneElement(child, { style })
        }

        return child
    })

    return (
        <td
            className={classNames({ 'd-none': !needRender, 'no-wrap': !textWrap })}
            colSpan={colSpan}
            rowSpan={rowSpan}
        >
            <div className="n2o-advanced-table-cell-expand">{childrenWithProps}</div>
        </td>
    )
}

AdvancedTableCell.propTypes = {
    children: PropTypes.any,
    textWrap: PropTypes.bool,
    hasSpan: PropTypes.bool,
    record: PropTypes.object,
    needRender: PropTypes.bool,
}

AdvancedTableCell.defaultProps = {
    hasSpan: false,
    record: {},
    textWrap: true,
    needRender: true,
}

export default pure(AdvancedTableCell)
