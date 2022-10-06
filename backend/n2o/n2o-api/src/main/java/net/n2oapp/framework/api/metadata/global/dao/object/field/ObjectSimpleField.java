package net.n2oapp.framework.api.metadata.global.dao.object.field;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.global.dao.object.AbstractParameter;

/**
 * Исходная модель простого поля объекта.
 */
@Getter
@Setter
@NoArgsConstructor
public class ObjectSimpleField extends AbstractParameter {
    private String domain;
    private String defaultValue;
    private String param;
    private String validationFailKey;

    public ObjectSimpleField(ObjectSimpleField field) {
        super(field);
        this.setDomain(field.getDomain());
        this.setDefaultValue(field.getDefaultValue());
        this.setParam(field.getParam());
        this.setValidationFailKey(field.getValidationFailKey());
    }
}
