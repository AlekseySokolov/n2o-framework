import React from 'react'
import isEqual from 'lodash/isEqual'
import each from 'lodash/each'
import concat from 'lodash/concat'
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import PropTypes from 'prop-types'
import cx from 'classnames'

import {
    showFields,
    hideFields,
    enableFields,
    disableFields,
} from '../../../actions/formPlugin'
import { makeGetResolveModelSelector } from '../../../selectors/models'

import FieldsetRow from './FieldsetRow'
import { resolveExpression } from './utils'

/**
 * Компонент - филдсет формы
 * @reactProps {array} rows - ряды, которые содержит филдсет. Они содержат колонки, которые содержат либо поля, либо филдсеты(филдсет рекрсивный).
 * @reactProps {string} className - класс компонента Fieldset
 * @reactProps {string} labelPosition - позиция лейбела относительно контрола: top-left, top-right, left, right.
 * @reactProps {string} label - заголовок филдсета
 * @reactProps {string} childrenLabel - заголовоки дочерних элементов  филдсета
 * @reactProps {array} labelWidth - ширина лейбела - Либо число, либо 'min' - займет минимальное возможное пространство, либо default - 100px
 * @reactProps {array} labelAlignment - выравнивание текста внутри лейбла
 * @reactProps {number} defaultCol
 * @reactProps {number} autoFocusId
 * @reactProps {node} component
 * @reactProps {node} children
 * @example
 *
 * //пример структуры rows
 * const rows = [
 *    {
 *      "cols": [
 *        {
 *          "fields": [
 *            {
 *            //...
 *            }
 *          ]
 *        },
 *        {
 *          "fields": [
 *            {
 *            //...
 *            }
 *          ]
 *        },
 *        {
 *          "fields": [
 *            {
 *            //...
 *            }
 *          ]
 *        },
 *      ]
 *    },
 *    {
 *      "cols": [
 *        {
 *          "fieldsets": [
 *            {
 *            "rows": [
 *            //...
 *            ]
 *            }
 *          ]
 *        },
 *        {
 *          "fields": [
 *            {
 *            //...
 *            },
 *            {
 *            //...
 *            }
 *          ]
 *        },
 *      ]
 *    }
 *  ]
 *
 *  <Fieldset rows={rows}>
 *
 */

class Fieldset extends React.Component {
    constructor(props) {
        super(props)

        this.setVisible = this.setVisible.bind(this)
        this.setEnabled = this.setEnabled.bind(this)
        this.getFormValues = this.getFormValues.bind(this)
        this.renderRow = this.renderRow.bind(this)

        this.state = {
            visible: true,
            enabled: true,
        }

        this.fields = []
    }

    componentDidMount() {
        this.resolveProperties()
    }

    componentDidUpdate(prevProps) {
        const { visible, enabled, activeModel } = this.props
        if (
            isEqual(activeModel, prevProps.activeModel) &&
      isEqual(visible, prevProps.visible) &&
      isEqual(enabled, prevProps.enabled)
        ) {
            return
        }
        this.resolveProperties()
    }

    resolveProperties() {
        const { visible, enabled, activeModel } = this.props

        const newEnabled = resolveExpression(enabled, activeModel)
        if (!isEqual(newEnabled, this.state.enabled)) {
            this.setEnabled(newEnabled)
        }

        const newVisible = resolveExpression(visible, activeModel)
        if (!isEqual(newVisible, this.state.visible)) {
            this.setVisible(newVisible)
        }
    }

    setVisible(nextVisibleField) {
        const { showFields, hideFields, form } = this.props
        this.setState(() => {
            if (nextVisibleField) {
                showFields(form, this.fields)
            } else {
                hideFields(form, this.fields)
            }
            return {
                visible: nextVisibleField,
            }
        })
    }

    setEnabled(nextEnabledField) {
        const { enableFields, disableFields, form } = this.props
        if (nextEnabledField) {
            enableFields(form, this.fields)
        } else {
            disableFields(form, this.fields)
        }
        this.setState({
            enabled: nextEnabledField,
        })
    }

    getFormValues(store) {
        const state = store.getState()
        return makeGetResolveModelSelector(this.props.form)(state)
    }

    calculateAllFields(rows) {
        let fields = []
        each(rows, (row) => {
            each(row.cols, (col) => {
                if (col.fieldsets) {
                    each(col.fieldsets, (fieldset) => {
                        fields = concat(fields, this.calculateAllFields(fieldset.rows))
                    })
                } else if (col.fields) {
                    each(col.fields, (field) => {
                        fields.push(field.id)
                    })
                }
            })
        })
        return fields
    }

    renderRow(rowId, row, props) {
        const {
            labelPosition,
            labelWidth,
            labelAlignment,
            defaultCol,
            autoFocusId,
            form,
            modelPrefix,
            autoSubmit,
            activeModel,
        } = this.props

        return (
            <FieldsetRow
                activeModel={activeModel}
                key={rowId}
                row={row}
                rowId={rowId}
                labelPosition={labelPosition}
                labelWidth={labelWidth}
                labelAlignment={labelAlignment}
                defaultCol={defaultCol}
                autoFocusId={autoFocusId}
                form={form}
                modelPrefix={modelPrefix}
                disabled={!this.state.enabled}
                autoSubmit={autoSubmit}
                {...props}
            />
        )
    }

    render() {
        const {
            className,
            style,
            component: ElementType,
            children,
            parentName,
            parentIndex,
            label,
            type,
            childrenLabel,
            ...rest
        } = this.props
        const { enabled, visible } = this.state

        this.fields = []
        const needLabel = label && type !== 'line'

        if (React.Children.count(children)) {
            return <ElementType>{children}</ElementType>
        }

        const classes = cx('n2o-fieldset', className, {
            'd-none': !visible,
        })

        return (
            <div className={classes} style={style}>
                {needLabel && <h4 className="n2o-fieldset__label">{label}</h4>}
                <ElementType
                    childrenLabel={childrenLabel}
                    enabled={enabled}
                    label={label}
                    type={type}
                    {...rest}
                    render={(rows, props = { parentName, parentIndex }) => {
                        this.fields = this.calculateAllFields(rows)
                        return rows.map((row, id) => this.renderRow(id, row, props))
                    }}
                />
            </div>
        )
    }
}

Fieldset.propTypes = {
    rows: PropTypes.array,
    className: PropTypes.string,
    label: PropTypes.string,
    childrenLabel: PropTypes.string,
    labelPosition: PropTypes.string,
    labelWidth: PropTypes.array,
    labelAlignment: PropTypes.array,
    defaultCol: PropTypes.number,
    autoFocusId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    component: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.node,
        PropTypes.func,
    ]),
    children: PropTypes.node,
    visible: PropTypes.oneOfType([PropTypes.bool, PropTypes.string]),
    enabled: PropTypes.oneOfType([PropTypes.bool, PropTypes.string]),
    dependency: PropTypes.array,
    form: PropTypes.string,
    showFields: PropTypes.func,
    hideFields: PropTypes.func,
    enableFields: PropTypes.func,
    disableFields: PropTypes.func,
    modelPrefix: PropTypes.string,
}

Fieldset.defaultProps = {
    labelPosition: 'top-left',
    component: 'div',
}

Fieldset.contextTypes = {
    store: PropTypes.object,
}

const mapDispatchToProps = dispatch => bindActionCreators(
    {
        showFields,
        hideFields,
        enableFields,
        disableFields,
    },
    dispatch,
)

const FieldsetContainer = connect(
    null,
    mapDispatchToProps,
)(Fieldset)

export default FieldsetContainer
