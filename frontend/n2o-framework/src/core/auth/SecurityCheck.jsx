import React from 'react'
import PropTypes from 'prop-types'
import isEqual from 'lodash/isEqual'
import omit from 'lodash/omit'

import { resolveLinksRecursively } from '../../utils/linkResolver'

import withSecurity from './withSecurity'
import { SECURITY_CHECK } from './authTypes'

/**
 * <SecurityCheck config={{roles: ['admin'], context: ['ivanov']]}}
 *                render={(permissions, err) => permissions ? <Input /> : <Alert message={err.message} />}
 */
class SecurityCheck extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            permissions: null,
            // eslint-disable-next-line react/no-unused-state
            error: null,
        }
    }

    async componentDidMount() {
        await this.checkPermissions(this.props)
    }

    // eslint-disable-next-line react/no-deprecated
    componentWillReceiveProps(nextProps) {
        const { user, config, store } = this.props
        const resolvedConfig = resolveLinksRecursively(config, store)
        const nextResolvedConfig = resolveLinksRecursively(config, nextProps.store)

        if (
            !isEqual(nextProps.user, user) ||
            !isEqual(nextProps.config, config) ||
            !isEqual(nextResolvedConfig, resolvedConfig)
        ) {
            this.checkPermissions(nextProps, nextResolvedConfig)
        }
    }

    async checkPermissions(params, resolvedConfig) {
        const { authProvider, config, user, store } = params
        const { onPermissionsSet } = this.props

        try {
            const permissions = await authProvider(SECURITY_CHECK, {
                store,
                user,
                config,
                resolvedConfig,
            })

            this.setState(
                // eslint-disable-next-line react/no-unused-state
                { permissions, error: null },
                () => onPermissionsSet && onPermissionsSet(permissions),
            )
        } catch (error) {
            this.setState(
                // eslint-disable-next-line react/no-unused-state
                { permissions: null, error },
                () => onPermissionsSet && onPermissionsSet(null),
            )
        }
    }

    render() {
        const { permissions } = this.state
        const { render } = this.props
        const props = omit(this.props, ['authProvider', 'config'])

        return render({ permissions, ...props })
    }
}

SecurityCheck.propTypes = {
    store: PropTypes.object,
    user: PropTypes.object,
    config: PropTypes.object,
    render: PropTypes.func,
    onPermissionsSet: PropTypes.func,
}

SecurityCheck.defaultProps = {
    store: {},
    onPermissionsSet: () => {},
}

export default withSecurity(SecurityCheck)
