import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Provider } from 'react-redux';
import { pick, keys } from 'lodash';
import { compose, withContext, defaultProps, withProps } from 'recompose';
import { IntlProvider, addLocaleData } from 'react-intl';

import history from './history';
import configureStore from './store';

import FactoryProvider from './core/factory/FactoryProvider';
import createFactoryConfig, {
  factories,
} from './core/factory/createFactoryConfig';
import factoryConfigShape from './core/factory/factoryConfigShape';

import apiProvider from './core/api';
import SecurityProvider from './core/auth/SecurityProvider';

import Router from './components/core/Router';

import ruLocaleData from 'react-intl/locale-data/ru';
import Application from './components/core/Application';
import { HeaderFooterTemplate } from './components/core/templates';
import DefaultBreadcrumb from './components/core/Breadcrumb/DefaultBreadcrumb';
import globalFnDate from './utils/globalFnDate';
import configureErrorPages from './components/errors';

addLocaleData(ruLocaleData);

class N2o extends Component {
  constructor(props) {
    super(props);
    const config = {
      security: props.security,
      messages: props.messages,
      customReducers: props.customReducers,
      customSagas: props.customSagas,
      apiProvider: props.apiProvider,
    };
    this.store = configureStore({}, history, config);
    globalFnDate.addFormat(props.formats);
  }

  generateCustomConfig() {
    return pick(this.props, keys(factories));
  }

  render() {
    const { security, realTimeConfig, embeddedRouting, children } = this.props;

    const config = createFactoryConfig(this.generateCustomConfig());

    return (
      <Provider store={this.store}>
        <SecurityProvider {...security}>
          <Application
            realTimeConfig={realTimeConfig}
            render={({ locale, messages }) => (
              <IntlProvider locale={locale} messages={messages}>
                <FactoryProvider
                  config={config}
                  securityBlackList={['actions']}
                >
                  <Router embeddedRouting={embeddedRouting}>{children}</Router>
                </FactoryProvider>
              </IntlProvider>
            )}
          />
        </SecurityProvider>
      </Provider>
    );
  }
}

N2o.propTypes = {
  ...factoryConfigShape,
  defaultTemplate: PropTypes.oneOfType([
    PropTypes.func,
    PropTypes.element,
    PropTypes.node,
  ]),
  defaultBreadcrumb: PropTypes.oneOfType([
    PropTypes.func,
    PropTypes.element,
    PropTypes.node,
  ]),
  defaultPromptMessage: PropTypes.string,
  formats: PropTypes.shape({
    dateFormat: PropTypes.string,
    timeFormat: PropTypes.string,
  }),
  security: PropTypes.shape({
    authProvider: PropTypes.func,
    redirectPath: PropTypes.string,
    externalLoginUrl: PropTypes.string,
    loginComponent: PropTypes.element,
    userMenuComponent: PropTypes.element,
    forbiddenComponent: PropTypes.element,
  }),
  messages: PropTypes.shape({
    timeout: PropTypes.shape({
      error: PropTypes.number,
      success: PropTypes.number,
      warning: PropTypes.number,
      info: PropTypes.number,
    }),
  }),
  customReducers: PropTypes.object,
  customSagas: PropTypes.array,
  customErrorPages: PropTypes.object,
  apiProvider: PropTypes.func,
  realTimeConfig: PropTypes.bool,
  embeddedRouting: PropTypes.bool,
  children: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.node),
    PropTypes.node,
  ]),
};

const EnhancedN2O = compose(
  defaultProps({
    defaultTemplate: HeaderFooterTemplate,
    defaultBreadcrumb: DefaultBreadcrumb,
    defaultPromptMessage:
      'Все несохраненные данные будут утеряны, вы уверены, что хотите уйти?',
    defaultErrorPages: configureErrorPages(),
    formats: {
      dateFormat: 'DD.MM.YY',
      timeFormat: 'HH:mm:ss',
    },
    security: {},
    messages: {},
    customReducers: {},
    customSagas: [],
    apiProvider,
    realTimeConfig: true,
    embeddedRouting: true,
  }),
  withContext(
    {
      defaultTemplate: PropTypes.oneOfType([
        PropTypes.func,
        PropTypes.element,
        PropTypes.node,
      ]),
      defaultBreadcrumb: PropTypes.oneOfType([
        PropTypes.func,
        PropTypes.element,
        PropTypes.node,
      ]),
      defaultPromptMessage: PropTypes.string,
      defaultErrorPages: PropTypes.arrayOf(
        PropTypes.oneOfType([PropTypes.node, PropTypes.element, PropTypes.func])
      ),
    },
    props => ({
      defaultTemplate: props.defaultTemplate,
      defaultBreadcrumb: props.defaultBreadcrumb,
      defaultPromptMessage: props.defaultPromptMessage,
      defaultErrorPages: props.defaultErrorPages,
    })
  ),
  withProps(props => ({
    ref: props.forwardedRef,
  }))
)(N2o);

// This works! Because forwardedRef is now treated like a regular prop.
export default React.forwardRef(({ ...props }, ref) => (
  <EnhancedN2O {...props} forwardedRef={ref} />
));
