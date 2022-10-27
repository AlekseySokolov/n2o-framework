import React from 'react'
import PropTypes from 'prop-types'
import { ButtonGroup, Button } from 'reactstrap'
import { useTranslation } from 'react-i18next'

function Buttons({ visible, disabled, searchLabel, resetLabel, onSearch, onReset, className }) {
    const { t } = useTranslation()

    return visible ? (
        <ButtonGroup className={className}>
            <Button disabled={disabled} color="primary" onClick={onSearch}>
                {searchLabel || t('search')}
            </Button>
            <Button disabled={disabled} color="secondary" onClick={onReset}>
                {resetLabel || t('reset')}
            </Button>
        </ButtonGroup>
    ) : null
}

Buttons.propTypes = {
    className: PropTypes.string,
    onSearch: PropTypes.func,
    onReset: PropTypes.func,
    searchLabel: PropTypes.string,
    resetLabel: PropTypes.string,
    visible: PropTypes.bool,
    disabled: PropTypes.bool,
}

Buttons.defaultProps = {
    onSearch: () => {},
    onReset: () => {},
    visible: true,
}

export default Buttons
