package net.n2oapp.framework.api.metadata.event.action;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.global.view.action.control.Target;

/**
 * Абстрактное действие, содержащее стандартные саги
 */
@Getter
@Setter
public abstract class N2oAbstractMetaAction extends N2oAbstractAction {
    private Boolean closeOnSuccess;
    private Boolean doubleCloseOnSuccess;
    private Boolean closeOnFail;
    private String redirectUrl;
    private Target redirectTarget;
    private Boolean refreshOnSuccess;
    private String[] refreshDatasources;

    @Deprecated
    public String getRefreshWidgetId() {
        return refreshDatasources != null && refreshDatasources.length > 0 ? refreshDatasources[0] : null;
    }

    @Deprecated
    public void setRefreshWidgetId(String refreshWidgetId) {
        this.refreshDatasources = new String[] {refreshWidgetId};
    }
}
