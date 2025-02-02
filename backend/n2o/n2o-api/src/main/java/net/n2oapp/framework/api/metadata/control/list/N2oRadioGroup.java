package net.n2oapp.framework.api.metadata.control.list;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.N2oAttribute;
import net.n2oapp.framework.api.metadata.N2oComponent;

/**
 * Компонент радио кнопок
 */
@Getter
@Setter
@N2oComponent
public class N2oRadioGroup extends N2oSingleListFieldAbstract implements Inlineable {
    @N2oAttribute("Отображение элементов на одной строке")
    private Boolean inline;
    @N2oAttribute("Тип кнопок")
    private RadioGroupType type;
}
