import React from 'react';
import PropTypes from 'prop-types';
import classNames from 'classnames';
import isString from 'lodash/isString';
import Button from 'reactstrap/lib/Button';
import {
  compose,
  withState,
  withHandlers,
  lifecycle,
  defaultProps,
} from 'recompose';

import InputText from '../../controls/InputText/InputText';

let timeoutId = null;
const ENTER_KEY_CODE = 13;

const SearchTrigger = {
  ENTER: 'ENTER',
  CHANGE: 'CHANGE',
  BUTTON: 'BUTTON',
};

function SearchBar({
  className,
  innerValue,
  icon,
  button,
  onClick,
  onChange,
  onKeyDown,
  placeholder,
  iconClear,
  onClear,
}) {
  const hasInnerValue = innerValue !== undefined && innerValue !== '';
  const isIconClear = iconClear && hasInnerValue;

  return (
    <div className={classNames('n2o-search-bar', className)}>
      <span className="n2o-search-bar__control">
        <InputText
          onKeyDown={onKeyDown}
          value={innerValue}
          onChange={onChange}
          placeholder={placeholder}
        />
        {isIconClear && (
          <i
            className="n2o-search-bar__clear-icon fa fa-times"
            onClick={onClear}
          />
        )}
        {isString(icon) ? <i className={icon} /> : icon}
      </span>
      {!!button && (
        <Button {...button} onClick={onClick}>
          {button.label}
          {button.icon && <i className={classNames('ml-2', button.icon)} />}
        </Button>
      )}
    </div>
  );
}

SearchBar.propTypes = {
  /**
   * Класс компонента
   */
  className: PropTypes.string,
  /**
   * Начальное состояние строки поиска
   */
  initialValue: PropTypes.string,
  /**
   * Значение компонента
   */
  value: PropTypes.string,
  /**
   * Placeholder контрола
   */
  placeholder: PropTypes.string,
  /**
   * Триггер запуска колбека поиска
   */
  trigger: PropTypes.oneOf([
    SearchTrigger.CHANGE,
    SearchTrigger.ENTER,
    SearchTrigger.BUTTON,
  ]),
  /**
   * Настройка кнопки
   */
  button: PropTypes.object,
  /**
   * Иконка
   */
  icon: PropTypes.oneOfType([
    PropTypes.func,
    PropTypes.node,
    PropTypes.element,
    PropTypes.string,
  ]),
  /**
   * Коллбек поиска
   */
  onSearch: PropTypes.func,
  /**
   * Delay поиска при change триггере
   */
  throttleDelay: PropTypes.number,
};

SearchBar.defaultProps = {
  trigger: SearchTrigger.CHANGE,
  button: false,
  icon: 'fa fa-search',
  iconClear: true,
  onSearch: () => {},
};

const enhance = compose(
  defaultProps({
    trigger: SearchTrigger.CHANGE,
    throttleDelay: 400,
  }),
  withState(
    'innerValue',
    'setInnerValue',
    ({ value, initialValue }) => initialValue || value
  ),
  withHandlers({
    onClick: ({ innerValue, onSearch }) => () => onSearch(innerValue),
    onKeyDown: ({ innerValue, trigger, onSearch }) => ({ keyCode }) => {
      if (trigger === SearchTrigger.ENTER && keyCode === ENTER_KEY_CODE) {
        onSearch(innerValue);
      }
    },
    onChange: ({
      setInnerValue,
      trigger,
      throttleDelay,
      onSearch,
    }) => value => {
      setInnerValue(value);

      if (value === '') {
        onSearch(null);
      }

      if (trigger === SearchTrigger.CHANGE) {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => onSearch(value), throttleDelay);
      }
    },
    onClear: ({ setInnerValue, onSearch }) => () => {
      setInnerValue(null);
      onSearch(null);
    },
  }),
  lifecycle({
    componentDidMount() {
      const { initSearchValue, setInnerValue, onSearch } = this.props;

      if (initSearchValue !== undefined) {
        setInnerValue(initSearchValue);
        onSearch('');
      }
    },
    componentDidUpdate(prevProps) {
      const { value, setInnerValue } = this.props;

      if (prevProps.value !== value) {
        setInnerValue(value);
      }
    },
  })
);

export { SearchBar };
export default enhance(SearchBar);
