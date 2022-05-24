import React from 'react'
import PropTypes from 'prop-types'
import classNames from 'classnames'
import { NavbarBrand } from 'reactstrap'

import { NavbarBrandContent } from './NavbarBrandContent'

export function Logo({ title, subtitle, className, style, href, src, showContent, isMiniView }) {
    return (
        <section
            className={classNames(
                'n2o-header-logo d-flex flex-wrap justify-content-center',
                className,
                {
                    visible: showContent,
                    mini: isMiniView,
                },
            )}
            style={style}
        >
            {src && (
                <NavbarBrand className="n2o-brand" href={href}>
                    <NavbarBrandContent brandImage={src} />
                </NavbarBrand>
            )}
            {title && (
                <a href={href} className="navbar-brand logo-title">
                    {title}
                </a>
            )}
            {subtitle && (
                <small className="navbar-brand logo-subtitle">
                    {subtitle}
                </small>
            )}
        </section>
    )
}

Logo.propTypes = {
    title: PropTypes.string,
    subtitle: PropTypes.string,
    className: PropTypes.string,
    style: PropTypes.object,
    href: PropTypes.string,
    src: PropTypes.string,
    showContent: PropTypes.bool,
    isMiniView: PropTypes.bool,
}

Logo.defaultProps = {
    href: '/',
    showContent: true,
}
