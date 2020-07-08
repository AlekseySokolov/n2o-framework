package net.n2oapp.framework.autotest.api.component.widget.calendar;

import net.n2oapp.framework.autotest.api.component.Component;

/**
 * Событие календаря для автотестирования
 */
public interface CalendarEvent extends Component {
    void shouldHaveTitle(String title);

    void shouldNotHaveTitle();
}
