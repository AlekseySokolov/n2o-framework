package net.n2oapp.framework.autotest.impl.component.snippet;

import com.codeborne.selenide.Condition;
import net.n2oapp.framework.api.metadata.meta.badge.Position;
import net.n2oapp.framework.autotest.Colors;
import net.n2oapp.framework.autotest.api.component.snippet.Status;

import java.time.Duration;

/**
 * Компонент отображения статуса для автотестирования
 */
public class N2oStatus extends N2oSnippet implements Status {
    @Override
    public void shouldHaveText(String text, Duration... duration) {
        should(Condition.text(text), element().$(".n2o-status-text_text"), duration);
    }

    @Override
    public void shouldHaveTextPosition(Position position) {
        element().shouldHave(Condition.cssClass(String.format("n2o-status-text__%s", position.getId())));
    }

    @Override
    public void shouldHaveColor(Colors color) {
        element().$(".n2o-status-text_icon__right, .n2o-status-text_icon__left")
                .shouldHave(Condition.cssClass(String.format("bg-%s", color.name().toLowerCase())));
    }
}
