package net.n2oapp.framework.autotest.impl.component.region;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import net.n2oapp.framework.autotest.Colors;
import net.n2oapp.framework.autotest.N2oSelenide;
import net.n2oapp.framework.autotest.api.collection.Widgets;
import net.n2oapp.framework.autotest.api.component.region.PanelRegion;

/**
 * Регион в виде панели для автотестирования
 */
public class N2oPanelRegion extends N2oRegion implements PanelRegion {
    @Override
    public Widgets content() {
        return N2oSelenide.collection(element().$$(".n2o-standard-widget-layout"), Widgets.class);
    }

    // TODO убрать и заменить на 2 метода
    @Override
    public void toggleCollapse() {
        collapseToggleBtn().click();
    }

    @Override
    public void shouldHaveTitle(String title) {
        element().$(".card-header").shouldHave(Condition.text(title));
    }

    @Override
    public void shouldNotHaveTitle() {
        element().$(".card-header").shouldNot(Condition.exist);
    }

    @Override
    public void shouldHaveFooterTitle(String footer) {
        element().$(".card-footer").shouldHave(Condition.text(footer));
    }

    @Override
    public void expandContent() {
        if (collapseToggleBtn().is(collapsedCondition()))
            collapseToggleBtn().click();
    }

    @Override
    public void collapseContent() {
        if (!collapseToggleBtn().is(collapsedCondition()))
            collapseToggleBtn().click();
    }

    @Override
    public void shouldBeCollapsible() {
        collapseToggleBtn().shouldBe(Condition.exist);
    }

    @Override
    public void shouldNotBeCollapsible() {
        collapseToggleBtn().shouldNotBe(Condition.exist);
    }

    @Override
    public void shouldBeExpanded() {
        collapseToggleBtn().shouldNotHave(collapsedCondition());
    }

    @Override
    public void shouldBeCollapsed() {
        collapseToggleBtn().shouldHave(collapsedCondition());
    }

    @Override
    public void shouldHaveBorderColor(Colors color) {
        element().shouldHave(Condition.cssClass("border-" + color.toString().toLowerCase()));
    }

    @Override
    public void shouldHaveIcon(String icon) {
        element().$(".card-header .n2o-icon").shouldHave(Condition.cssClass(icon));
    }

    private SelenideElement collapseToggleBtn() {
        return element().$("button.collapse-toggle");
    }

    private Condition collapsedCondition() {
        return Condition.cssClass("collapse-toggle--up");
    }
}
