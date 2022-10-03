package net.n2oapp.framework.autotest.breadcrumbs;

import net.n2oapp.framework.autotest.api.component.button.StandardButton;
import net.n2oapp.framework.autotest.api.component.page.SimplePage;
import net.n2oapp.framework.autotest.api.component.page.StandardPage;
import net.n2oapp.framework.autotest.api.component.widget.FormWidget;
import net.n2oapp.framework.autotest.api.component.widget.table.TableWidget;
import net.n2oapp.framework.autotest.run.AutoTestBase;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.pack.N2oAllDataPack;
import net.n2oapp.framework.config.metadata.pack.N2oAllPagesPack;
import net.n2oapp.framework.config.metadata.pack.N2oApplicationPack;
import net.n2oapp.framework.config.selective.CompileInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BreadcrumbsAT extends AutoTestBase {

    @BeforeAll
    public static void beforeClass() {
        configureSelenide();
    }

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oAllPagesPack(), new N2oApplicationPack(), new N2oAllDataPack());
    }

    @Test
    public void breadcrumbsTest() {
        builder.sources(
                new CompileInfo("net/n2oapp/framework/autotest/breadcrumbs/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/breadcrumbs/reader.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/breadcrumbs/book.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/breadcrumbs/readers.query.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/breadcrumbs/books.query.xml"));
        SimplePage page = open(SimplePage.class);
        page.shouldExists();
        page.breadcrumb().titleByIndexShouldHaveText("Table", 0);
        checkPageAndClickRow(page, "reader1", "reader2", 0);

        page.shouldExists();
        page.breadcrumb().titleByIndexShouldHaveText("Table", 0);
        page.breadcrumb().titleByIndexShouldHaveText("reader1", 1);
        checkPageAndClickRow(page, "book1", "book2", 1);

        page.shouldExists();
        page.breadcrumb().titleByIndexShouldHaveText("Table", 0);
        page.breadcrumb().titleByIndexShouldHaveText("reader1", 1);
        page.breadcrumb().titleByIndexShouldHaveText("book2", 2);
        checkPageAndClickRow(page, "reader1", "reader2", 1);

        page.shouldExists();
        page.breadcrumb().titleByIndexShouldHaveText("Table", 0);
        page.breadcrumb().titleByIndexShouldHaveText("reader2", 1);
        checkPageAndClickRow(page, "book1", "book2", 0);

        page.shouldExists();
        page.breadcrumb().titleByIndexShouldHaveText("Table", 0);
        page.breadcrumb().titleByIndexShouldHaveText("reader2", 1);
        page.breadcrumb().titleByIndexShouldHaveText("book1", 2);
    }

    @Test
    public void testPage() {
        builder.sources(
                new CompileInfo("net/n2oapp/framework/autotest/breadcrumbs/page/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/breadcrumbs/page/page2.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/breadcrumbs/page/page3.page.xml"));

        StandardPage page = open(StandardPage.class);
        page.shouldExists();

        page.breadcrumb().titleShouldHaveText("Тест настройки бредкрампа на странице");
        page.toolbar().bottomLeft().button("Вторая страница").click();
        page.breadcrumb().crumb(0).shouldHaveLabel("Первая страница");
        page.breadcrumb().crumb(0).shouldHaveLink(getBaseUrl() + "/#");
        page.breadcrumb().crumb(1).shouldHaveLabel("Вторая страница");
        page.breadcrumb().crumb(1).shouldHaveLink(getBaseUrl() + "/#/page2");
        String url = getBaseUrl();
        page.breadcrumb().crumb(1).click();
        page.shouldExists();
        Assertions.assertEquals(url, getBaseUrl());

        page.breadcrumb().crumb(0).click();
        page.shouldExists();
        page.breadcrumb().titleShouldHaveText("Тест настройки бредкрампа на странице");
        page.toolbar().bottomLeft().button("Вторая страница").click();
        page.toolbar().bottomLeft().button("Третья страница").click();
        page.breadcrumb().crumb(0).shouldHaveLabel("Вторая страница");
        page.breadcrumb().crumb(0).shouldHaveLink(getBaseUrl() + "/#/page2");
        page.breadcrumb().crumb(1).shouldHaveLabel("Третья страница");
        page.breadcrumb().crumb(1).shouldHaveLink(getBaseUrl() + "/#/page2/page3");
        page.breadcrumb().crumb(2).shouldHaveLabel("Нет ссылки");
        url = getBaseUrl();
        page.breadcrumb().crumb(2).click();
        Assertions.assertEquals(url, getBaseUrl());
    }

    @Test
    public void testRelativeLinks() {
        builder.sources(
                new CompileInfo("net/n2oapp/framework/autotest/breadcrumbs/relative_links/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/breadcrumbs/relative_links/page2.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/breadcrumbs/relative_links/page3.page.xml"));

        StandardPage page = open(StandardPage.class);
        page.shouldExists();
        String url = getBaseUrl();
        page.toolbar().bottomLeft().button("Вторая страница").click();
        page.breadcrumb().crumb(0).shouldHaveLabel("Первая страница");
        page.breadcrumb().crumb(0).shouldHaveLink(getBaseUrl() + "/#");
        page.breadcrumb().crumb(1).shouldHaveLabel("Вторая страница");
        page.breadcrumb().crumb(1).shouldHaveLink(getBaseUrl() + "/#/page2");

        page.breadcrumb().crumb(0).click();
        page.shouldExists();
        Assertions.assertEquals(url, getBaseUrl());
        page.toolbar().bottomLeft().button("Вторая страница").click();
        page.shouldExists();
        page.toolbar().bottomLeft().button("Третья страница").click();
        page.breadcrumb().crumb(0).shouldHaveLink(getBaseUrl() + "/#");
        page.breadcrumb().crumb(1).shouldHaveLink(getBaseUrl() + "/#/page2");
    }

    private void checkPageAndClickRow(SimplePage page, String firstTableRow, String secondTableRow, Integer clickRow) {
        StandardButton openButton = page.widget(FormWidget.class)
                .toolbar()
                .topLeft()
                .button(0, StandardButton.class);
        openButton.shouldExists();
        openButton.shouldHaveLabel("Open");

        TableWidget table = page.widget(TableWidget.class);
        table.shouldExists();
        table.columns().headers().shouldHaveSize(1);
        TableWidget.Rows rows = table.columns().rows();
        rows.shouldHaveSize(2);

        rows.row(0).cell(0).textShouldHave(firstTableRow);
        rows.row(1).cell(0).textShouldHave(secondTableRow);

        rows.row(clickRow).click();
        openButton.click();
    }
}