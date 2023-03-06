package net.n2oapp.framework.autotest.impl.component.region;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import net.n2oapp.framework.autotest.N2oSelenide;
import net.n2oapp.framework.autotest.api.component.region.RegionItems;
import net.n2oapp.framework.autotest.api.component.region.ScrollspyRegion;
import net.n2oapp.framework.autotest.impl.component.N2oComponent;

/**
 * Регион с отслеживанием прокрутки для автотестирования
 */
public class N2oScrollspyRegion extends N2oRegion implements ScrollspyRegion {

    @Override
    public ContentItem contentItem(int index) {
        return new N2oContentItem(element().$$(".n2o-scroll-spy-region__content-wrapper").get(index));
    }

    @Override
    public ContentItem contentItem(String title) {
        return new N2oContentItem(element().$$(".n2o-scroll-spy-region__content-wrapper").findBy(Condition.text(title)));
    }

    @Override
    public void activeContentItemShouldHaveTitle(String title) {
        element().$(".n2o-scroll-spy-region__content-wrapper.active").shouldHave(Condition.text(title));
    }

    @Override
    public void activeMenuItemShouldHaveTitle(String title) {
        menu().element().$(".n2o-scroll-spy-region__menu-item.active").shouldHave(Condition.text(title));
    }

    @Override
    public Menu menu() {
        return new N2oMenu(element().$(".n2o-scroll-spy-region__menu-wrapper"));
    }

    @Override
    public void menuShouldHavePosition(MenuPosition position) {
        switch (position) {
            case left:
                element().parent().$(".position-right").shouldNotBe(Condition.exist);
                break;
            case right:
                element().parent().$(".position-right").shouldHave(Condition.exist);
                break;
        }
    }

    public static class N2oContentItem extends N2oRegion implements ContentItem {
        public N2oContentItem(SelenideElement element) {
            setElement(element);
        }

        @Override
        public RegionItems content() {
            return N2oSelenide.collection(firstLevelElements(".n2o-scroll-spy-region__content", "div"), RegionItems.class);
        }

    }

    public static class N2oMenu extends N2oComponent implements Menu {

        public N2oMenu(SelenideElement element) {
            setElement(element);
        }

        @Override
        public void shouldHaveTitle(String title) {
            element().$(".n2o-scroll-spy-region__menu-title").shouldHave(Condition.text(title));
        }

        @Override
        public MenuItem menuItem(int index) {
            return new N2oMenuItem(element().$$(".n2o-scroll-spy-region__menu-item").get(index));
        }

        @Override
        public DropdownMenuItem dropdownMenuItem(int index) {
            return new N2oDropdownMenuItem(element().$$(".n2o-scroll-spy-region__dropdown-menu-items-wrapper").get(index));
        }

        @Override
        public MenuItem menuItem(String title) {
            return new N2oMenuItem(element().$$(".n2o-scroll-spy-region__menu-item").findBy(Condition.text(title)));
        }

        @Override
        public DropdownMenuItem dropdownMenuItem(String label) {
            return new N2oDropdownMenuItem(element().$$(".n2o-scroll-spy-region__dropdown-menu-items-wrapper").findBy(Condition.text(label)));
        }
    }

    public static class N2oMenuItem extends N2oComponent implements MenuItem {

        public N2oMenuItem(SelenideElement element) {
            setElement(element);
        }

        @Override
        public void shouldHaveText(String text) {
            element().shouldHave(Condition.text(text));
        }

        @Override
        public void click() {
            element().click();
        }
    }

    public static class N2oDropdownMenuItem extends N2oComponent implements DropdownMenuItem {

        public N2oDropdownMenuItem(SelenideElement element) {
            setElement(element);
        }

        @Override
        public void shouldHaveText(String text) {
            element().shouldHave(Condition.text(text));
        }

        @Override
        public MenuItem menuItem(int index) {
            return new N2oMenuItem(element().$$(".n2o-scroll-spy-region__menu-item").get(index));
        }

        @Override
        public MenuItem menuItem(String title) {
            return new N2oMenuItem(element().$$(".n2o-scroll-spy-region__menu-item").findBy(Condition.text(title)));
        }

        @Override
        public void click() {
            element().$(".n2o-scroll-spy-region__dropdown-toggle").click();
        }
    }
}
