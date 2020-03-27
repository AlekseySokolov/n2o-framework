package net.n2oapp.framework.autotest.impl.component.fieldset;

import com.codeborne.selenide.Condition;
import net.n2oapp.framework.autotest.api.component.fieldset.FieldSet;
import net.n2oapp.framework.autotest.impl.component.N2oComponent;

/**
 * Филдсет для автотестирования
 */
public abstract class N2oFieldSet extends N2oComponent implements FieldSet {
    @Override
    public void shouldBeEmpty() {
        element().shouldBe(Condition.empty);
    }
}
