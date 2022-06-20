package net.n2oapp.framework.config.metadata.compile.widget;

import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.global.view.page.N2oStandardDatasource;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oCustomWidget;
import net.n2oapp.framework.api.metadata.local.CompiledObject;
import net.n2oapp.framework.api.metadata.meta.widget.CustomWidget;
import net.n2oapp.framework.config.metadata.compile.page.PageScope;
import org.springframework.stereotype.Component;

@Component
public class CustomWidgetCompiler extends BaseWidgetCompiler<CustomWidget, N2oCustomWidget> {
    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oCustomWidget.class;
    }

    @Override
    protected String getPropertyWidgetSrc() {
        return null;
    }

    @Override
    public CustomWidget compile(N2oCustomWidget source, CompileContext<?, ?> context, CompileProcessor p) {
        CustomWidget widget = new CustomWidget();
        N2oStandardDatasource datasource = initInlineDatasource(widget, source, p);
        CompiledObject object = getObject(source, datasource, p);
        compileBaseWidget(widget, source, context, p, object);
        WidgetScope widgetScope = new WidgetScope(source.getId(), source.getDatasourceId(), ReduxModel.resolve, p.getScope(PageScope.class));
        MetaActions widgetActions = initMetaActions(source, p);
        compileToolbarAndAction(widget, source, context, p, widgetScope, widgetActions, object, null);
        return widget;
    }
}
