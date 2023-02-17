package net.n2oapp.framework.autotest.impl.component.application;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import net.n2oapp.framework.autotest.api.component.application.Footer;
import net.n2oapp.framework.autotest.impl.component.N2oComponent;

/**
 * Компонент Footer для автотестирования
 */
public class N2oFooter extends N2oComponent implements Footer {

    public N2oFooter(SelenideElement element) {
        setElement(element);
    }

    @Override
    public void leftTextShouldHaveText(String name) {
        element().$(".text-left").shouldHave(Condition.exactText(name));
    }

    @Override
    public void rightTextShouldHaveText(String name) {
        element().$(".text-right").shouldHave(Condition.exactText(name));
    }
}
