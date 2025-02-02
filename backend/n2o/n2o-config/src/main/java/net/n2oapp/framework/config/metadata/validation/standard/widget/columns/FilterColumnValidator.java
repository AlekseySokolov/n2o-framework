package net.n2oapp.framework.config.metadata.validation.standard.widget.columns;

import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.aware.SourceClassAware;
import net.n2oapp.framework.api.metadata.compile.SourceProcessor;
import net.n2oapp.framework.api.metadata.global.view.widget.table.column.N2oFilterColumn;
import net.n2oapp.framework.api.metadata.validate.SourceValidator;
import net.n2oapp.framework.config.metadata.compile.widget.WidgetScope;
import org.springframework.stereotype.Component;

@Component
public class FilterColumnValidator implements SourceValidator<N2oFilterColumn>, SourceClassAware {

    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oFilterColumn.class;
    }

    @Override
    public void validate(N2oFilterColumn source, SourceProcessor p) {
        WidgetScope widgetScope = p.getScope(WidgetScope.class);
        if (source.getFilter() != null)
            p.validate(source.getFilter(), widgetScope);

        if (source.getCell() != null)
            p.validate(source.getCell(), widgetScope);
    }
}
