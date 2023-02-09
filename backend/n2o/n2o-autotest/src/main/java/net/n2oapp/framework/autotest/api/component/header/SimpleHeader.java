package net.n2oapp.framework.autotest.api.component.header;

import net.n2oapp.framework.autotest.api.collection.Menu;
import net.n2oapp.framework.autotest.api.component.Component;

/**
 * Компонент header для автотестирования
 */
public interface SimpleHeader extends Component {

    void shouldHaveBrandName(String brandName);

    Menu nav();

    Menu extra();

    SearchBar search();

    void shouldHaveSidebarSwitcher();

    void switchSidebar();

}
