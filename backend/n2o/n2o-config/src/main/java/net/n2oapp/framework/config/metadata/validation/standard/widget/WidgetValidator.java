package net.n2oapp.framework.config.metadata.validation.standard.widget;

import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.aware.SourceClassAware;
import net.n2oapp.framework.api.metadata.compile.SourceProcessor;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oWidget;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.N2oButton;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.N2oSubmenu;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.N2oToolbar;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.ToolbarItem;
import net.n2oapp.framework.api.metadata.validate.SourceValidator;
import net.n2oapp.framework.config.metadata.compile.datasource.DatasourceIdsScope;
import net.n2oapp.framework.config.metadata.compile.widget.WidgetScope;
import net.n2oapp.framework.config.metadata.validation.standard.ValidationUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Валидатор виджета
 */
@Component
public class WidgetValidator implements SourceValidator<N2oWidget>, SourceClassAware {

    @Override
    public void validate(N2oWidget n2oWidget, SourceProcessor p) {
        DatasourceIdsScope datasourceIdsScope = p.getScope(DatasourceIdsScope.class);
        if (n2oWidget.getDatasource() != null) {
            WidgetScope widgetScope = new WidgetScope(n2oWidget.getId(), null, null, null);
            p.validate(n2oWidget.getDatasource(), widgetScope, datasourceIdsScope);
        }

        if (n2oWidget.getToolbars() != null) {
            List<N2oButton> menuItems = new ArrayList<>();
            for (N2oToolbar toolbar : n2oWidget.getToolbars()) {
                if (toolbar.getItems() != null) {
                    for (ToolbarItem item : toolbar.getItems()) {
                        if (item instanceof N2oButton) {
                            menuItems.add((N2oButton) item);
                        } else if (item instanceof N2oSubmenu) {
                            menuItems.addAll(Arrays.asList(((N2oSubmenu) item).getMenuItems()));
                        }
                    }
                }
            }
            p.safeStreamOf(menuItems).forEach(menuItem -> p.validate(menuItem, datasourceIdsScope));
            p.checkIdsUnique(menuItems, "Кнопка '{0}' встречается более чем один раз в виджете '" + n2oWidget.getId() + "'!");
        }

        if (n2oWidget.getDatasourceId() != null) {
            checkDatasource(n2oWidget, datasourceIdsScope);
        }
        p.safeStreamOf(n2oWidget.getActions()).forEach(actionsBar -> p.validate(actionsBar.getAction()));
    }

    /**
     * Проверка существования источника данных, на который ссылается виджет
     * @param n2oWidget Виджет
     * @param scope     Скоуп источников данных
     */
    private void checkDatasource(N2oWidget n2oWidget, DatasourceIdsScope scope) {
        ValidationUtils.checkForExistsDatasource(n2oWidget.getDatasourceId(), scope,
                String.format("Виджет %s cсылается на несуществующий источник данных '%s'", ValidationUtils.getIdOrEmptyString(n2oWidget.getId()), n2oWidget.getDatasourceId()));
    }

    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oWidget.class;
    }
}
