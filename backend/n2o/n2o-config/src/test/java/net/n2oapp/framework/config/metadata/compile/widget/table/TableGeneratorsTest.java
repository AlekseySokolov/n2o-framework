package net.n2oapp.framework.config.metadata.compile.widget.table;

import net.n2oapp.framework.api.metadata.meta.page.StandardPage;
import net.n2oapp.framework.api.metadata.meta.widget.table.Table;
import net.n2oapp.framework.api.metadata.meta.widget.toolbar.AbstractButton;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import net.n2oapp.framework.config.metadata.pack.N2oActionsPack;
import net.n2oapp.framework.config.metadata.pack.N2oAllDataPack;
import net.n2oapp.framework.config.metadata.pack.N2oControlsPack;
import net.n2oapp.framework.config.metadata.pack.N2oFieldSetsPack;
import net.n2oapp.framework.config.metadata.pack.N2oPagesPack;
import net.n2oapp.framework.config.metadata.pack.N2oRegionsPack;
import net.n2oapp.framework.config.metadata.pack.N2oWidgetsPack;
import net.n2oapp.framework.config.selective.CompileInfo;
import net.n2oapp.framework.config.test.SourceCompileTestBase;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TableGeneratorsTest extends SourceCompileTestBase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oWidgetsPack(), new N2oActionsPack(), new N2oAllDataPack(), new N2oPagesPack(),
                        new N2oFieldSetsPack(), new N2oControlsPack(), new N2oRegionsPack())
                .sources(new CompileInfo("net/n2oapp/framework/config/metadata/compile/stub/utBlank.object.xml"),
                        new CompileInfo("net/n2oapp/framework/config/metadata/compile/stub/utBlank.page.xml"),
                        new CompileInfo("net/n2oapp/framework/config/metadata/compile/stub/utBlank.widget.xml"),
                        new CompileInfo("net/n2oapp/framework/config/metadata/compile/stub/utBlank.query.xml"));
    }

    @Test
    public void generateCrud() {
        StandardPage page = (StandardPage) compile("net/n2oapp/framework/config/metadata/compile/toolbar/generate/crud.page.xml")
                .get(new PageContext("crud"));
        Table t = (Table) page.getRegions().get("single").get(0).getContent().get(0);

        assertThat(t.getToolbar().size(), is(1));
        assertThat(t.getToolbar().get("topRight").get(0).getButtons().size(), is(3));
        assertThat(t.getToolbar().get("topRight").get(0).getButtons().get(0).getId(), is("create"));
        assertThat(t.getToolbar().get("topRight").get(0).getButtons().get(1).getId(), is("update"));
        assertThat(t.getToolbar().get("topRight").get(0).getButtons().get(2).getId(), is("delete"));
    }

    @Test
    public void generateTableSettings() {
        StandardPage page = (StandardPage) compile("net/n2oapp/framework/config/metadata/compile/toolbar/generate/table_settings.page.xml")
                .get(new PageContext("table_settings"));
        Table t = (Table) page.getRegions().get("single").get(0).getContent().get(0);

        assertThat(t.getToolbar().get("topLeft").get(0).getButtons().size(), is(5));

        AbstractButton filtersBtn = t.getToolbar().get("topLeft").get(0).getButtons().get(0);
        AbstractButton columnsBtn = t.getToolbar().get("topLeft").get(0).getButtons().get(1);
        AbstractButton refreshBtn = t.getToolbar().get("topLeft").get(0).getButtons().get(2);
        AbstractButton resizeBtn = t.getToolbar().get("topLeft").get(0).getButtons().get(3);
        AbstractButton wordwrapBtn = t.getToolbar().get("topLeft").get(0).getButtons().get(4);

        assertThat(filtersBtn.getHint(), is("Изменить видимость фильтров"));
        assertThat(filtersBtn.getIcon(), is("fa fa-filter"));
        assertThat(columnsBtn.getSrc(), is("ToggleColumn"));
        assertThat(columnsBtn.getHint(), is("Изменить видимость колонок"));
        assertThat(columnsBtn.getIcon(), is("fa fa-table"));
        assertThat(refreshBtn.getHint(), is("Обновить данные"));
        assertThat(refreshBtn.getIcon(), is("fa fa-refresh"));
        assertThat(resizeBtn.getSrc(), is("ChangeSize"));
        assertThat(resizeBtn.getHint(), is("Изменить размер"));
        assertThat(resizeBtn.getIcon(), is("fa fa-bars"));
        assertThat(wordwrapBtn.getSrc(), is("WordWrap"));
        assertThat(wordwrapBtn.getHint(), is("Перенос по словам"));
        assertThat(wordwrapBtn.getIcon(), is("fa-solid fa-grip-lines"));
    }

    @Test
    public void generateColumns() {
        StandardPage page = (StandardPage) compile("net/n2oapp/framework/config/metadata/compile/toolbar/generate/columns.page.xml")
                .get(new PageContext("columns"));
        Table t = (Table) page.getRegions().get("single").get(0).getContent().get(0);

        assertThat(t.getToolbar().get("bottomRight").get(0).getButtons().size(), is(1));

        AbstractButton button = t.getToolbar().get("bottomRight").get(0).getButtons().get(0);

        assertThat(button.getSrc(), is("ToggleColumn"));
        assertThat(button.getHint(), is("Изменить видимость колонок"));
        assertThat(button.getIcon(), is("fa fa-table"));
    }

    @Test
    public void generateFilters() {
        StandardPage page = (StandardPage) compile("net/n2oapp/framework/config/metadata/compile/toolbar/generate/filters.page.xml")
                .get(new PageContext("filters"));
        Table t = (Table) page.getRegions().get("single").get(0).getContent().get(0);

        assertThat(t.getToolbar().get("bottomRight").get(0).getButtons().size(), is(1));

        AbstractButton button = t.getToolbar().get("bottomRight").get(0).getButtons().get(0);

        assertThat(button.getHint(), is("Изменить видимость фильтров"));
        assertThat(button.getIcon(), is("fa fa-filter"));
    }

    @Test
    public void generateRefresh() {
        StandardPage page = (StandardPage) compile("net/n2oapp/framework/config/metadata/compile/toolbar/generate/refresh.page.xml")
                .get(new PageContext("refresh"));
        Table t = (Table) page.getRegions().get("single").get(0).getContent().get(0);

        assertThat(t.getToolbar().get("bottomRight").get(0).getButtons().size(), is(1));

        AbstractButton button = t.getToolbar().get("bottomRight").get(0).getButtons().get(0);

        assertThat(button.getHint(), is("Обновить данные"));
        assertThat(button.getIcon(), is("fa fa-refresh"));
    }

    @Test
    public void generateResize() {
        StandardPage page = (StandardPage) compile("net/n2oapp/framework/config/metadata/compile/toolbar/generate/resize.page.xml")
                .get(new PageContext("resize"));
        Table t = (Table) page.getRegions().get("single").get(0).getContent().get(0);

        assertThat(t.getToolbar().get("bottomRight").get(0).getButtons().size(), is(1));

        AbstractButton button = t.getToolbar().get("bottomRight").get(0).getButtons().get(0);

        assertThat(button.getSrc(), is("ChangeSize"));
        assertThat(button.getHint(), is("Изменить размер"));
        assertThat(button.getIcon(), is("fa fa-bars"));
    }

    @Test
    public void generateWordWrap() {
        StandardPage page = (StandardPage) compile("net/n2oapp/framework/config/metadata/compile/toolbar/generate/wordwrap.page.xml")
                .get(new PageContext("wordwrap"));
        Table t = (Table) page.getRegions().get("single").get(0).getContent().get(0);

        assertThat(t.getToolbar().get("bottomRight").get(0).getButtons().size(), is(1));

        AbstractButton button = t.getToolbar().get("bottomRight").get(0).getButtons().get(0);

        assertThat(button.getSrc(), is("WordWrap"));
        assertThat(button.getHint(), is("Перенос по словам"));
        assertThat(button.getIcon(), is("fa-solid fa-grip-lines"));
    }
}
