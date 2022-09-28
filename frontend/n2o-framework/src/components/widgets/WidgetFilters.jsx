import React from 'react'
import PropTypes from 'prop-types'
import { connect, ReactReduxContext } from 'react-redux'
import { getFormValues, reset } from 'redux-form'
import isEqual from 'lodash/isEqual'
import difference from 'lodash/difference'
import map from 'lodash/map'
import unset from 'lodash/unset'
import debounce from 'lodash/debounce'
import { createStructuredSelector } from 'reselect'

import { Filter } from '../snippets/Filter/Filter'
import { makeWidgetFilterVisibilitySelector } from '../../ducks/widgets/selectors'
import propsResolver from '../../utils/propsResolver'
import { generateFormFilterId } from '../../utils/generateFormFilterId'
import { FILTER_DELAY } from '../../constants/time'
import { ModelPrefix } from '../../core/datasource/const'
import { validate as validateFilters } from '../../core/validation/validate'

import { flatFields, getFieldsKeys } from './Form/utils'
import ReduxForm from './Form/ReduxForm'

/**
 * Компонент WidgetFilters
 * @reactProps {string} widgetId
 * @reactProps {array} fieldsets
 * @reactProps {boolean} visible
 * @reactProps {boolean} hideButtons
 * @reactProps {array} blackResetList
 * @reactProps {object} filterModel
 * @reactProps {function} fetchWidget
 * @reactProps {function} clearFilterModel
 * @reactProps {function} reduxFormFilter
 */
class WidgetFilters extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            defaultValues: props.filterModel,
        }
        this.formName = generateFormFilterId(props.datasource)
        this.handleFilter = this.handleFilter.bind(this)
        this.handleReset = this.handleReset.bind(this)
        this.debouncedHandleFilter = debounce(this.handleFilter, FILTER_DELAY)
    }

    getChildContext() {
        return {
            _widgetFilter: {
                formName: this.formName,
                filter: this.handleFilter,
                reset: this.handleReset,
            },
        }
    }

    componentDidUpdate(prevProps) {
        const { filterModel, reduxFormFilter, setFilter, validate, searchOnChange } = this.props
        const { defaultValues } = this.state

        if (!isEqual(prevProps.filterModel, filterModel)) {
            const { store } = this.context
            const state = store.getState()

            validate(state, this.formName)
        }

        if (isEqual(reduxFormFilter, filterModel)) { return }

        if (
            !isEqual(prevProps.filterModel, filterModel) &&
            !isEqual(filterModel, defaultValues)
        ) {
            this.setState({
                defaultValues: filterModel,
            })
            if (searchOnChange) {
                this.handleFilter()
            }
        } else if (!isEqual(reduxFormFilter, prevProps.reduxFormFilter)) {
            setFilter(reduxFormFilter)
            if (searchOnChange) {
                this.debouncedHandleFilter()
            }
        }
    }

    handleFilter() {
        const { fetchData } = this.props

        fetchData({ page: 1 })
    }

    handleReset() {
        const {
            fieldsets,
            blackResetList,
            reduxFormFilter,
            resetFilterModel,
            setFilter,
        } = this.props
        const newReduxForm = { ...reduxFormFilter }
        const toReset = difference(
            map(flatFields(fieldsets, []), 'id'),
            blackResetList,
        )

        toReset.forEach((field) => {
            unset(newReduxForm, field)
        })

        /*
          fakeDefaultValues HACK!
          для button выполняющего redux-form/RESET
          Если defaultValues = {} и newReduxForm = {}
          redux-form не кидает reinitialize из за того что defaultValues не поменялись,
          -> не срабатывают field dependency завязанные на actionTypes.INITIALIZE
          (прим. не меняется enabled зависимого поля)
        */

        const fakeDefaultValues = Date.now()

        this.setState({ defaultValues: fakeDefaultValues },
            () => this.setState(
                {
                    defaultValues: newReduxForm,
                },
                () => {
                    resetFilterModel(this.formName)
                    setFilter(newReduxForm)
                    this.handleFilter()
                },
            ))
    }

    static getDerivedStateFromProps(props, state) {
        const { fieldsets } = props
        const resolved = Object.values(propsResolver(fieldsets) || {})

        if (isEqual(resolved, state.fieldsets)) {
            return null
        }

        const fields = getFieldsKeys(resolved)

        return {
            fieldsets: resolved,
            fields,
        }
    }

    render() {
        const {
            visible,
            hideButtons,
            validation,
            filterModel,
        } = this.props
        const { defaultValues, fieldsets, fields } = this.state

        return (
            <Filter
                style={{ display: !visible ? 'none' : '' }}
                hideButtons={hideButtons}
                onSearch={this.handleFilter}
                onReset={this.handleReset}
            >
                <ReduxForm
                    form={this.formName}
                    fieldsets={fieldsets}
                    fields={fields}
                    activeModel={filterModel}
                    initialValues={defaultValues}
                    validation={validation}
                    modelPrefix={ModelPrefix.filter}
                />
            </Filter>
        )
    }
}

WidgetFilters.propTypes = {
    datasource: PropTypes.string,
    fieldsets: PropTypes.array,
    visible: PropTypes.bool,
    blackResetList: PropTypes.array,
    filterModel: PropTypes.object,
    validation: PropTypes.object,
    reduxFormFilter: PropTypes.oneOfType([PropTypes.func, PropTypes.object]),
    setFilter: PropTypes.func,
    fetchData: PropTypes.func,
    hideButtons: PropTypes.bool,
    searchOnChange: PropTypes.bool,
    validate: PropTypes.func,
    resetFilterModel: PropTypes.func,
}

WidgetFilters.defaultProps = {
    hideButtons: false,
    searchOnChange: false,
}

WidgetFilters.contextType = ReactReduxContext

WidgetFilters.childContextTypes = {
    _widgetFilter: PropTypes.object.isRequired,
}

const mapStateToProps = createStructuredSelector({
    visible: (state, props) => makeWidgetFilterVisibilitySelector(props.widgetId)(state, props),
    reduxFormFilter: (state, props) => getFormValues(generateFormFilterId(props.datasource))(state) || {},
})

const mapDispatchToProps = dispatch => ({
    dispatch,
    resetFilterModel: formName => dispatch(reset(formName)),
    validate: (state, datasourceId) => validateFilters(
        state,
        datasourceId,
        ModelPrefix.filter,
        dispatch,
    ),
})

export default connect(mapStateToProps, mapDispatchToProps)(WidgetFilters)
