package net.n2oapp.framework.api.metadata.action;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.aware.DatasourceIdAware;
import net.n2oapp.framework.api.metadata.control.PageRef;

/**
 * Исходная модель действия обновления данных виджета
 */
@Getter
@Setter
public class N2oRefreshAction extends N2oAbstractAction implements N2oAction, DatasourceIdAware {
    private String datasourceId;
    private PageRef page;

    @Deprecated
    public String getWidgetId() {
        return datasourceId;
    }

    @Deprecated
    public void setWidgetId(String widgetId) {
        this.datasourceId = widgetId;
    }
}
