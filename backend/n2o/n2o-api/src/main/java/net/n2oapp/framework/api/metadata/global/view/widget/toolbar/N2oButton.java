package net.n2oapp.framework.api.metadata.global.view.widget.toolbar;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.N2oAttribute;
import net.n2oapp.framework.api.metadata.N2oComponent;
import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.action.N2oAction;
import net.n2oapp.framework.api.metadata.action.N2oConfirmAction;
import net.n2oapp.framework.api.metadata.action.ifelse.N2oIfBranchAction;
import net.n2oapp.framework.api.metadata.aware.WidgetIdAware;
import net.n2oapp.framework.api.metadata.meta.action.condition.ConditionAction;
import net.n2oapp.framework.api.metadata.meta.action.condition.ConditionActionPayload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.n2oapp.framework.api.StringUtils.*;

/**
 * Исходная модель кнопки
 */
@Getter
@Setter
@N2oComponent
public class N2oButton extends N2oAbstractButton implements Button, WidgetIdAware {
    private String actionId;
    private N2oAction[] actions;
    @N2oAttribute("Круглая форма")
    private Boolean rounded;
    private Boolean validate;
    private String[] validateDatasourceIds;

    @Deprecated
    private String confirm;
    @Deprecated
    private ConfirmType confirmType;
    @Deprecated
    private String confirmText;
    @Deprecated
    private String confirmTitle;
    @Deprecated
    private String confirmOkLabel;
    @Deprecated
    private String confirmOkColor;
    @Deprecated
    private String confirmCancelLabel;
    @Deprecated
    private String confirmCancelColor;

    @N2oAttribute("Недоступность кнопки при пустой модели")
    private DisableOnEmptyModelType disableOnEmptyModel;

    private Dependency[] dependencies;

    private Boolean isGeneratedForSubMenu = false;

    @Override
    public List<N2oAction> getListActions() {
        if (actions == null)
            return new ArrayList<>();
        return Arrays.asList(actions);
    }

    @Getter
    @Setter
    public static class Dependency implements Source {
        private String value;
        private String datasource;
        private ReduxModel model;

        @Deprecated
        public String getRefWidgetId() {
            return datasource;
        }

        @Deprecated
        public void setRefWidgetId(String refWidgetId) {
            this.datasource = refWidgetId;
        }
    }

    @Getter
    @Setter
    public static class EnablingDependency extends Dependency {
        private String message;
    }

    public static class VisibilityDependency extends Dependency {
    }

    @Deprecated
    public void adapterV2() {
        if (confirm != null && !confirm.equals("false")) {
            N2oConfirmAction confirmAction = new N2oConfirmAction();
            confirmAction.setType(confirmType);
            confirmAction.setText(confirmText);
            confirmAction.setTitle(confirmTitle);
            N2oConfirmAction.ConfirmButton[] confirmButtons = new N2oConfirmAction.ConfirmButton[2];
            confirmButtons[0] = new N2oConfirmAction.OkButton();
            confirmButtons[1] = new N2oConfirmAction.CancelButton();
            if (confirmOkLabel != null || confirmOkColor != null) {
                confirmButtons[0].setLabel(confirmOkLabel);
                confirmButtons[0].setColor(confirmOkColor);
            }
            if (confirmCancelLabel != null || confirmCancelColor != null) {
                confirmButtons[1].setLabel(confirmCancelLabel);
                confirmButtons[1].setColor(confirmCancelColor);
            }
            confirmAction.setConfirmButtons(confirmButtons);

            N2oAction outAction;
            if (isLink(confirm)) {
                outAction = new N2oIfBranchAction();
                ((N2oIfBranchAction) outAction).setTest(unwrapLink(confirm));
                ((N2oIfBranchAction) outAction).setDatasourceId(getDatasourceId());
                ((N2oIfBranchAction) outAction).setActions(new N2oAction[]{confirmAction});
            } else {
                outAction = confirmAction;
            }

            if (actions == null) {
                actions = new N2oAction[1];
                actions[0] = outAction;
            } else {
                N2oAction[] out = new N2oAction[actions.length + 1];
                System.arraycopy(actions, 0, out, 1, actions.length);
                out[0] = outAction;
                actions = out;
            }
        }
    }
}

