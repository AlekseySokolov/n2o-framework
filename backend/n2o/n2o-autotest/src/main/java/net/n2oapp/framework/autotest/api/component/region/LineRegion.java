package net.n2oapp.framework.autotest.api.component.region;

import net.n2oapp.framework.autotest.api.component.Expandable;

/**
 * Регион с горизонтальным делителем для автотестирования
 */
public interface LineRegion extends Region, Expandable {
    RegionItems content();

    void shouldBeCollapsible();

    void shouldNotBeCollapsible();

    void shouldHaveLabel(String title);
}
