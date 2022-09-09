package net.n2oapp.framework.autotest.api.component.cell;

import net.n2oapp.framework.autotest.Colors;
import net.n2oapp.framework.autotest.api.component.badge.Badge;

/**
 * Ячейка таблицы с текстом для автотестирования
 */
public interface BadgeCell extends Cell, Badge {
    void colorShouldBe(Colors color);

    void textShouldHave(String text);
}
