package net.n2oapp.framework.config.metadata.compile.widget;

import lombok.Getter;
import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.global.view.page.datasource.N2oStandardDatasource;
import net.n2oapp.framework.config.metadata.compile.page.PageScope;

import java.io.Serializable;

/**
 * Информация о виджете при сборке внутренних метаданных
 */
@Getter
public class WidgetScope implements Serializable {
    private String clientWidgetId;
    private String widgetId;
    private String datasourceId;
    private String globalDatasourceId;
    private ReduxModel model;
    private N2oStandardDatasource inLineDatasource;


    public WidgetScope(String widgetId, String datasourceId, N2oStandardDatasource inLineDatasource) {
        this.widgetId = widgetId;
        this.datasourceId = datasourceId;
        this.inLineDatasource = inLineDatasource;
    }

    public WidgetScope(String widgetId, String datasourceId, ReduxModel model, PageScope pageScope) {
        this.widgetId = widgetId;
        this.datasourceId = datasourceId;
        this.model = model;
        this.clientWidgetId = pageScope != null ? pageScope.getGlobalWidgetId(widgetId) : widgetId;
        this.globalDatasourceId = pageScope != null ? pageScope.getClientDatasourceId(datasourceId) : datasourceId;
    }
}
