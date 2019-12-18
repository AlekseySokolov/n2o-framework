import React from 'react';
import PropTypes from 'prop-types';
import StandardWidget from '../StandardWidget';
import HtmlContainer from './HtmlContainer';
import dependency from '../../../core/dependency';

/**
 * HtmlWidget
 * @reactProps {string} containerId - id конейтенера
 * @reactProps {string} pageId - id страницы
 * @reactProps {boolean} fetchOnInit - фетчить / не фетчить данные при инициализации
 * @reactProps {boolean} url - url для фетчинга
 * @reactProps {string} widgetId - id виджета
 * @reactProps {string} html - html код
 * @reactProps {object} dataProvider
 * @reactProps {object} datasource
 */

//JSON - метаданные
// {
//   "widgets": {
//   "Page_Html": {
//     "html": {
//       "html": "<h1>Some title</h1>"
//     }
//   }
//  }
// }

//JSON - метаданные  + фича resolvePlaceholders (resolve на datasource[0])
// {
//   "widgets": {
//   "Page_Html": {
//     "resolvePlaceholders" : true,
//     "html": {
//       "html": "<div class='jumbotron'><h4>user: :username :surname / username: :username / surname: :surname</h4></div><div class='jumbotron'><ul><li>:username</li><li>:surname</li></ul></div>"
//     }
//   }
//  }
// }

// username и surname из метаданных при "resolvePlaceholders" : true, заменятся на соответствующие ключи в html (прим. :username :surname)
// list: [
//   {
//     username: 'Афанасий',
//     surname: 'Ревин',
//   },
// ],

class HtmlWidget extends React.Component {
  /**
   * Мэппинг пропсов
   */
  getWidgetProps() {
    return {
      id: this.props.id,
      url: this.props.url,
      dataProvider: this.props.dataProvider,
      datasource: this.props.datasource,
      ...this.props.html,
    };
  }

  /**
   * Базовый рендер
   */
  render() {
    const {
      fetchOnInit,
      id: widgetId,
      toolbar,
      actions,
      className,
      style,
      pageId,
      resolvePlaceholders,
      datasource,
    } = this.props;
    return (
      <StandardWidget
        widgetId={widgetId}
        toolbar={toolbar}
        actions={actions}
        className={className}
        fetchOnInit={fetchOnInit}
        style={style}
      >
        <HtmlContainer
          pageId={pageId}
          widgetId={widgetId}
          fetchOnInit={fetchOnInit}
          resolvePlaceholders={resolvePlaceholders}
          datasource={datasource}
          {...this.getWidgetProps()}
        />
      </StandardWidget>
    );
  }
}

HtmlWidget.defaultProps = {
  toolbar: {},
};

HtmlWidget.propTypes = {
  containerId: PropTypes.string.isRequired,
  pageId: PropTypes.string.isRequired,
  fetchOnInit: PropTypes.bool,
  url: PropTypes.bool,
  widgetId: PropTypes.string,
  toolbar: PropTypes.object,
  actions: PropTypes.object,
  html: PropTypes.string,
  dataProvider: PropTypes.object,
};

export default dependency(HtmlWidget);
