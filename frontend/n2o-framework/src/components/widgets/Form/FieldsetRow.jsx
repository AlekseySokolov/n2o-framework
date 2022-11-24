import React from 'react'
import PropTypes from 'prop-types'
import { pure } from 'recompose'
import { Row } from 'reactstrap'

// eslint-disable-next-line import/no-cycle
import { FieldsetCol } from './FieldsetCol'

function FieldsetRow({ rowId, row, ...rest }) {
    return (
        <Row key={rowId} {...row.props} className={row.className} style={row.style}>
            {row.cols &&
        row.cols.map((col, colId) => (
            // eslint-disable-next-line react/no-array-index-key
            <FieldsetCol key={colId} col={col} colId={colId} {...rest} />
        ))}
        </Row>
    )
}

FieldsetRow.propTypes = {
    rowId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    row: PropTypes.object,
    colId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
}

export default pure(FieldsetRow)
