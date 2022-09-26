import React from 'react'
import PropTypes from 'prop-types'

import Buttons from '../../../../snippets/Filter/Buttons'

/**
 * Компонент обертка для встраивания кнопок фильтра в любое место формы, как Field.
 * Методы берутся из контекста, поэтому обязательно использовать внутри WidgetFilters
 * @reactProps {boolean} visible
 * @reactProps {string} searchLabel
 * @reactProps {string} resetLabel
 */

// eslint-disable-next-line react/prefer-stateless-function
class FilterButtonsField extends React.Component {
    render() {
        const { visible, searchLabel, resetLabel, className, disabled } = this.props
        const { _widgetFilter } = this.context

        return visible ? (
            <Buttons
                className={className}
                disabled={disabled}
                searchLabel={searchLabel}
                resetLabel={resetLabel}
                onSearch={_widgetFilter.filter}
                onReset={_widgetFilter.reset}
            />
        ) : null
    }
}

FilterButtonsField.propTypes = {
    className: PropTypes.string,
    searchLabel: PropTypes.string,
    resetLabel: PropTypes.string,
    visible: PropTypes.bool,
    disabled: PropTypes.bool,
}

FilterButtonsField.defaultProps = {
    visible: true,
    disabled: false,
}

FilterButtonsField.contextTypes = {
    _widgetFilter: PropTypes.object,
}

export default FilterButtonsField
