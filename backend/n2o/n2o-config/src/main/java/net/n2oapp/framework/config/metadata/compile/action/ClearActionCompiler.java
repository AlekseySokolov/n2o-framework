package net.n2oapp.framework.config.metadata.compile.action;

import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.event.action.N2oClearAction;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.N2oButton;
import net.n2oapp.framework.api.metadata.meta.action.clear.ClearAction;
import net.n2oapp.framework.api.metadata.meta.saga.MetaSaga;
import net.n2oapp.framework.config.metadata.compile.ComponentScope;
import net.n2oapp.framework.config.metadata.compile.page.PageScope;
import net.n2oapp.framework.config.metadata.compile.widget.WidgetScope;
import org.springframework.stereotype.Component;

import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.property;

/**
 * Сборка действия очистки модели
 */
@Component
public class ClearActionCompiler extends AbstractActionCompiler<ClearAction, N2oClearAction> {
    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oClearAction.class;
    }

    @Override
    public ClearAction compile(N2oClearAction source, CompileContext<?, ?> context, CompileProcessor p) {
        initDefaults(source, context, p);
        ClearAction clearAction = new ClearAction();
        compileAction(clearAction, source, p);
        clearAction.setType(p.resolve(property("n2o.api.action.clear.type"), String.class));
        clearAction.getPayload().setPrefixes(initPayloadPrefixes(source, p));
        String widgetId = initClientWidgetId(context, p);
        PageScope pageScope = p.getScope(PageScope.class);
        clearAction.getPayload().setKey(
                pageScope == null || pageScope.getWidgetIdClientDatasourceMap() == null ||
                        pageScope.getWidgetIdClientDatasourceMap().get(widgetId) == null
                ? widgetId : pageScope.getWidgetIdClientDatasourceMap().get(widgetId));
        if (Boolean.TRUE.equals(source.getCloseOnSuccess())) {
            if (clearAction.getMeta() == null)
                clearAction.setMeta(new MetaSaga());
            clearAction.getMeta().setModalsToClose(1);
        }
        return clearAction;
    }

    private String[] initPayloadPrefixes(N2oClearAction source, CompileProcessor p) {
        String[] prefixes;
        WidgetScope widgetScope = p.getScope(WidgetScope.class);

        if (source.getModel() != null)
            prefixes = source.getModel();
        else if (widgetScope != null)
            prefixes = new String[]{widgetScope.getModel().getId()};
        else {
            N2oButton button = p.getScope(ComponentScope.class).unwrap(N2oButton.class);
            String model = button != null ? button.getModel().getId() : ReduxModel.resolve.getId();
            prefixes = new String[]{model};
        }
        return prefixes;
    }
}
