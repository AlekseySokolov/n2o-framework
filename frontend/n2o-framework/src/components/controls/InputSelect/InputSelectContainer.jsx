import React from 'react'
import { compose, setDisplayName } from 'recompose'
import PropTypes from 'prop-types'

import listContainer from '../listContainer'

// eslint-disable-next-line import/no-named-as-default
import InputSelect from './InputSelect'

/**
 * Контейнер для {@link InputSelect}
 * @reactProps {boolean} loading - флаг анимации загрузки
 * @reactProps {array} options - данные
 * @reactProps {string} valueFieldId - значение ключа value в данных
 * @reactProps {string} labelFieldId - значение ключа label в данных
 * @reactProps {string} iconFieldId - поле для иконки
 * @reactProps {string} imageFieldId - поле для картинки
 * @reactProps {object} badge - данные для баджа
 * @reactProps {boolean} disabled - флаг неактивности
 * @reactProps {boolean} disabled - только на чтение
 * @reactProps {array} disabledValues - неактивные данные
 * @reactProps {string} filter - варианты фильтрации
 * @reactProps {string} value - текущее значение
 * @reactProps {function} onInput - callback при вводе в инпут
 * @reactProps {function} onSelect - callback при выборе значения из popup
 * @reactProps {function} onScrollEnd - callback при прокрутке скролла popup
 * @reactProps {string} placeHolder - подсказка в инпуте
 * @reactProps {boolean} resetOnBlur - фича, при которой сбрасывается значение контрола, если оно не выбрано из popup
 * @reactProps {function} onOpen - callback на открытие попапа
 * @reactProps {function} onClose - callback на закрытие попапа
 * @reactProps {string} queryId - queryId
 * @reactProps {number} size - size
 * @reactProps {boolean} multiSelect - флаг мульти выбора
 * @reactProps {string} groupFieldId - поле для группировки
 * @reactProps {boolean} closePopupOnSelect - флаг закрытия попапа при выборе
 * @reactProps {boolean} hasCheckboxes - флаг наличия чекбоксов
 * @reactProps {string} format - формат
 * @reactProps {boolean} collapseSelected - флаг сжатия выбранных элементов
 * @reactProps {number} lengthToGroup - от скольки элементов сжимать выбранные элементы
 * @reactProps {function} fetchData
 * @reactProps {function} onSearch
 * @reactProps {boolean} openOnFocus
 */

class InputSelectContainer extends React.Component {
    constructor(props) {
        super(props)
        this.key = props.filterValues
        this.state = {
            resetMode: false,
        }
    }

    // eslint-disable-next-line react/no-deprecated
    componentWillReceiveProps(nextProps) {
        const resetMode = (nextProps.filterValues || []).reduce(
            (res, val) => res || val.resetMode,
            false,
        )
        const { value } = this.props

        if (resetMode && nextProps.value === value) {
            this.key = JSON.stringify(nextProps.filterValues)
            this.setState({ resetMode: true })
        } else {
            this.setState({ resetMode: false })
        }
    }

    render() {
        const { filter, data, isLoading, disabled, value } = this.props
        const { resetMode } = this.state

        const filterType = filter === 'server' ? false : filter

        return (
            <InputSelect
                {...this.props}
                options={data}
                value={!resetMode && value}
                filter={filterType}
                key={this.key}
                loading={isLoading}
                disabled={disabled}
            />
        )
    }
}

InputSelectContainer.propTypes = {
    loading: PropTypes.bool,
    options: PropTypes.array,
    valueFieldId: PropTypes.string.isRequired,
    labelFieldId: PropTypes.string.isRequired,
    iconFieldId: PropTypes.string,
    imageFieldId: PropTypes.string,
    badge: PropTypes.object,
    disabled: PropTypes.bool,
    disabledValues: PropTypes.array,
    filter: PropTypes.oneOf(['includes', 'startsWith', 'endsWith', 'server']),
    value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    onInput: PropTypes.func,
    onSelect: PropTypes.func,
    onScrollEnd: PropTypes.func,
    placeholder: PropTypes.string,
    flip: PropTypes.bool,
    resetOnBlur: PropTypes.bool,
    onOpen: PropTypes.func,
    onClose: PropTypes.func,
    queryId: PropTypes.string.isRequired,
    size: PropTypes.number.isRequired,
    multiSelect: PropTypes.bool,
    groupFieldId: PropTypes.string,
    closePopupOnSelect: PropTypes.bool,
    hasCheckboxes: PropTypes.bool,
    format: PropTypes.string,
    collapseSelected: PropTypes.bool,
    lengthToGroup: PropTypes.number,
    fetchData: PropTypes.func,
    onSearch: PropTypes.func,
    autoFocus: PropTypes.bool,
    openOnFocus: PropTypes.bool,
    filterValues: PropTypes.any,
    data: PropTypes.any,
    isLoading: PropTypes.bool,
}

InputSelectContainer.defaultProps = {
    /**
     * Флаг загрузки
     */
    loading: false,
    /**
     * Флаг активности
     */
    disabled: false,
    /**
     * Неактивные данные
     */
    disabledValues: [],
    /**
     * Значение
     */
    value: '',
    /**
     * Фича, при которой сбрасывается значение контрола, если оно не выбрано из popup
     */
    resetOnBlur: false,
    /**
     * Варианты фильтрации
     */
    filter: false,
    /**
     * Мульти выбор значений
     */
    multiSelect: false,
    /**
     * Флаг закрытия попапа при выборе
     */
    closePopupOnSelect: true,
    /**
     * Флаг наличия чекбоксов в селекте
     */
    hasCheckboxes: false,
    /**
     * Флаг сжатия выбранных элементов
     */
    collapseSelected: true,
    /**
     * От скольки элементов сжимать выбранные элементы
     */
    lengthToGroup: 3,
    // eslint-disable-next-line react/default-props-match-prop-types
    expandPopUp: true,
    /**
     * Ключ id в данных
     */
    // eslint-disable-next-line react/default-props-match-prop-types
    valueFieldId: 'id',
    flip: false,
    /**
     * Авто фокусировка на селекте
     */
    autoFocus: false,
    /**
     * Флаг открытия попапа при фокусе на контроле
     */
    openOnFocus: false,
}

export default compose(
    setDisplayName('InputSelectContainer'),
    listContainer,
)(InputSelectContainer)
