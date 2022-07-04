package net.n2oapp.framework.config.metadata.validation.standard.widget;

import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.aware.SourceClassAware;
import net.n2oapp.framework.api.metadata.compile.SourceProcessor;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.api.metadata.validate.SourceValidator;
import net.n2oapp.framework.config.metadata.compile.widget.WidgetScope;
import net.n2oapp.framework.config.metadata.validation.standard.ValidationUtils;
import org.springframework.stereotype.Component;

/**
 * Валидатор виджета форма
 */
@Component
public class FormValidator implements SourceValidator<N2oForm>, SourceClassAware {

    @Override
    public void validate(N2oForm source, SourceProcessor p) {
        ValidationUtils.checkIds(source.getItems(), p);
        FieldsScope fieldsScope = new FieldsScope();
        WidgetScope widgetScope = new WidgetScope(source.getId(), source.getDatasourceId(), source.getDatasource());
        p.safeStreamOf(source.getItems()).forEach(item -> p.validate(item, fieldsScope, widgetScope));
        p.safeStreamOf(source.getActions()).forEach(actionsBar -> p.validate(actionsBar.getAction()));
    }

    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oForm.class;
    }
}