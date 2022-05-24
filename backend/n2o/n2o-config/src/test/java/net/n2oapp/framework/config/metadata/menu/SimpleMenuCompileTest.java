package net.n2oapp.framework.config.metadata.menu;

import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.application.Application;
import net.n2oapp.framework.api.metadata.global.view.widget.table.ImageShape;
import net.n2oapp.framework.api.metadata.header.HeaderItem;
import net.n2oapp.framework.api.metadata.header.SimpleMenu;
import net.n2oapp.framework.api.metadata.meta.page.Page;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.compile.application.ApplicationCompiler;
import net.n2oapp.framework.config.metadata.compile.application.ApplicationIOv2;
import net.n2oapp.framework.config.metadata.compile.context.ApplicationContext;
import net.n2oapp.framework.config.metadata.compile.menu.SimpleMenuCompiler;
import net.n2oapp.framework.config.metadata.compile.menu.SimpleMenuIOv3;
import net.n2oapp.framework.config.metadata.pack.N2oAllPagesPack;
import net.n2oapp.framework.config.selective.CompileInfo;
import net.n2oapp.framework.config.test.SourceCompileTestBase;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SimpleMenuCompileTest extends SourceCompileTestBase {
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oAllPagesPack());
        builder.ios(new SimpleMenuIOv3(), new ApplicationIOv2());
        builder.compilers(new SimpleMenuCompiler(), new ApplicationCompiler());
        builder.sources(new CompileInfo("net/n2oapp/framework/config/metadata/menu/testApplication.application.xml"),
                new CompileInfo("net/n2oapp/framework/config/metadata/menu/testMenu.page.xml"));
    }

    @Test
    public void testMenuItem() {
        Application application = read().compile().get(new ApplicationContext("testApplication"));
        SimpleMenu menu = application.getHeader().getMenu();
        HeaderItem menuItem = menu.getItems().get(0);

        assertThat(menuItem.getId(), is("notif"));
        assertThat(menuItem.getTitle(), is("Уведомления"));
        assertThat(menuItem.getIcon(), is("fa fa-bell"));
        assertThat(menuItem.getBadge(), is("2"));
        assertThat(menuItem.getBadgeColor(), is("warning"));
        assertThat(menuItem.getDatasource(), is("ds1"));
        assertThat(menuItem.getHref(), is("/login"));
        Page page = routeAndGet("/login", Page.class);
        assertThat(page, notNullValue());

        menuItem = menu.getItems().get(1);
        assertThat(menuItem.getTitle(), nullValue());
        assertThat(menuItem.getImageSrc(), is("/static/users/ivan90.png"));
        assertThat(menuItem.getImageShape(), is(ImageShape.square));
        assertThat(menuItem.getHref(), is("/logout"));
    }

    @Test
    public void testDropdownMenu() {
        Application application = read().compile().get(new ApplicationContext("testApplication"));
        SimpleMenu menu = application.getHeader().getMenu();
        HeaderItem dropdownMenu = menu.getItems().get(2);

        assertThat(dropdownMenu.getId(), is("user"));
        assertThat(dropdownMenu.getTitle(), is("Виктория"));
        assertThat(dropdownMenu.getImageSrc(), is("/static/users/vika91.png"));
        assertThat(dropdownMenu.getImageShape(), is(ImageShape.circle));

        HeaderItem subMenuItem = menu.getItems().get(2).getSubItems().get(0);
        assertThat(subMenuItem.getId(), is("mi4"));
        assertThat(subMenuItem.getTitle(), is("Профиль"));
        assertThat(subMenuItem.getIcon(), is("fa fa-user"));
        assertThat(subMenuItem.getHref(), is("/profile"));

        assertThat(dropdownMenu.getSubItems().size(), is(1));
//        assertThat(dropdownMenu.getSubItems().get(1).getType(), is("divider"));
//        assertThat(dropdownMenu.getSubItems().get(3).getType(), is("divider"));

        dropdownMenu = menu.getItems().get(3);
        assertThat(dropdownMenu.getTitle(), is("Сообщения"));
        assertThat(dropdownMenu.getIcon(), is("fa fa-bell"));
    }

    @Test
    public void testMenuItemWithPathParam() {
        Application application = read().compile().get(new ApplicationContext("testApplication"));
        HeaderItem item = application.getHeader().getMenu().getItems().get(4);

        assertThat(item.getId(), is("messages"));
        assertThat(item.getDatasource(), is("ds1"));
        assertThat(item.getType(), is("link"));
        assertThat(item.getHref(), is("/:id"));
        assertThat(item.getLinkType(), is(HeaderItem.LinkType.inner));
        assertThat(item.getPathMapping().size(), is(1));
        assertThat(item.getPathMapping().get("id").getModel(), is(ReduxModel.resolve));
        assertThat(item.getPathMapping().get("id").getDatasource(), is("ds1"));
        assertThat(item.getPathMapping().get("id").getBindLink(), is("models.resolve['ds1']"));
        assertThat(item.getPathMapping().get("id").getValue(), is("`id`"));
    }
}
