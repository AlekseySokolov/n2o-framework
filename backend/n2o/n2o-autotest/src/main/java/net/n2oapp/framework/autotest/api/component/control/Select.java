package net.n2oapp.framework.autotest.api.component.control;

import com.codeborne.selenide.Condition;
import net.n2oapp.framework.autotest.api.component.DropDown;
import net.n2oapp.framework.autotest.api.component.Expandable;
import net.n2oapp.framework.autotest.api.component.PopupControl;

/**
 * Компонент выбора из выпадающего списка для автотестирования
 */
public interface Select extends Control, PopupControl {

    void click();

    void find(String query);

    void shouldHaveOptions(String... options);

    void select(int index);

    void select(Condition by);

    void clear();

    void shouldBeCleanable();

    void shouldNotBeCleanable();

    void selectMulti(int... indexes);

    void shouldSelected(String value);

    void shouldBeChecked(int... indexes);

    void shouldNotBeChecked(int... indexes);

    void optionShouldHaveDescription(String option, String description);

    DropDown dropdown();
}
