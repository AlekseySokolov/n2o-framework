package net.n2oapp.framework.sandbox.autotest.examples;

import net.n2oapp.framework.autotest.N2oSelenide;
import net.n2oapp.framework.autotest.api.collection.Fields;
import net.n2oapp.framework.autotest.api.component.button.Button;
import net.n2oapp.framework.autotest.api.component.control.InputText;
import net.n2oapp.framework.autotest.api.component.modal.Modal;
import net.n2oapp.framework.autotest.api.component.page.SimplePage;
import net.n2oapp.framework.autotest.api.component.snippet.Alert;
import net.n2oapp.framework.autotest.api.component.widget.FormWidget;
import net.n2oapp.framework.autotest.api.component.widget.table.TableWidget;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.sandbox.autotest.SandboxAutotestApplication;
import net.n2oapp.framework.sandbox.autotest.SandboxAutotestBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "n2o.engine.test.classpath=/examples/crud_sql/",
        "n2o.sandbox.project-id=examples_crud_sql"},
        classes = SandboxAutotestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CrudSQLAT extends SandboxAutotestBase {

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
    }

    @Test
    public void crudTest() {
        SimplePage page = open(SimplePage.class);
        page.shouldExists();
        page.header().shouldHaveBrandName("N2O");
        page.breadcrumb().crumb(0).shouldHaveLabel("Список автомобилей");

        TableWidget table = page.widget(TableWidget.class);
        table.shouldExists();
        table.columns().headers().shouldHaveSize(3);
        TableWidget.Rows rows = table.columns().rows();
        table.paging().shouldHaveTotalElements(13);

        Button create = table.toolbar().topLeft().button("Создать");
        create.shouldExists();
        Button update = table.toolbar().topLeft().button("Изменить");
        update.shouldExists();
        Button delete = table.toolbar().topLeft().button("Удалить");
        delete.shouldExists();

        create.click();
        Modal modal = N2oSelenide.modal();
        modal.shouldExists();
        modal.shouldHaveTitle("Автомобили - Создание");

        Fields modalFields = modal.content(SimplePage.class).widget(FormWidget.class).fields();
        InputText inputName = modalFields.field("Марка")
                .control(InputText.class);
        inputName.shouldExists();
        InputText inputPrice = modalFields.field("Цена")
                .control(InputText.class);
        inputPrice.shouldExists();
        inputName.click();
        inputName.setValue("test-value");
        inputName.shouldHaveValue("test-value");
        inputPrice.click();
        inputPrice.setValue(((Long) 49999L).toString());
        inputPrice.shouldHaveValue(((Long) 49999L).toString());
        Button save = modal.toolbar().bottomRight().button("Сохранить");
        save.shouldExists();
        save.click();
        page.alerts(Alert.Placement.top).alert(0).shouldHaveText("Автомобиль добавлен в базу");
        table.paging().shouldHaveTotalElements(14);
        table.paging().selectPage("2");
        rows.row(3).cell(1).shouldHaveText("test-value");

        rows.shouldBeSelected(0);
        update.click();
        Modal modal1 = N2oSelenide.modal();
        modal1.shouldExists();
        modal1.shouldHaveTitle("Автомобили - Изменение");
        InputText inputName1 = modal1.content(SimplePage.class).widget(FormWidget.class).fields().field("Марка")
                .control(InputText.class);
        inputName1.shouldExists();
        InputText inputPrice1 = modal1.content(SimplePage.class).widget(FormWidget.class).fields().field("Цена")
                .control(InputText.class);
        inputPrice1.shouldExists();
        inputName1.click();
        inputName1.setValue("change-test-value");
        inputName1.shouldHaveValue("change-test-value");
        inputPrice1.click();
        inputPrice1.setValue(((Long) 39999L).toString());
        inputPrice1.shouldHaveValue(((Long) 39999L).toString());
        Button save1 = modal1.toolbar().bottomRight().button("Сохранить");
        save1.shouldExists();
        save1.click();
        page.alerts(Alert.Placement.top).alert(0).shouldHaveText("Данные об автомобиле изменены");
        table.paging().shouldHaveTotalElements(14);
        rows.row(0).cell(1).shouldHaveText("change-test-value");

        rows.shouldBeSelected(0);
        delete.click();
        page.dialog("Предупреждение").shouldBeVisible();
        page.dialog("Предупреждение").button("Да").click();
        page.alerts(Alert.Placement.top).alert(0).shouldHaveText("Данные об автомобиле удалены");
        table.paging().shouldHaveTotalElements(13);
    }

    @Test
    public void pagingTest() {
        SimplePage page = open(SimplePage.class);
        page.shouldExists();
        page.header().shouldHaveBrandName("N2O");
        page.breadcrumb().crumb(0).shouldHaveLabel("Список автомобилей");

        TableWidget table = page.widget(TableWidget.class);
        table.shouldExists();
        table.columns().headers().shouldHaveSize(3);
        table.paging().shouldHaveTotalElements(13);
        table.paging().shouldHaveActivePage("1");
        table.columns().rows().shouldHaveSize(10);

        table.paging().selectPage("2");
        table.paging().shouldHaveActivePage("2");
        table.columns().rows().shouldHaveSize(3);
    }

    @Test
    public void filterTest() {
        SimplePage page = open(SimplePage.class);
        page.shouldExists();
        page.header().shouldHaveBrandName("N2O");
        page.breadcrumb().crumb(0).shouldHaveLabel("Список автомобилей");

        TableWidget table = page.widget(TableWidget.class);
        table.shouldExists();
        table.columns().headers().shouldHaveSize(3);
        table.paging().shouldHaveTotalElements(13);

        table.filters().shouldBeVisible();
        InputText minPrice = table.filters().fields().field("Минимальная цена").control(InputText.class);
        minPrice.shouldExists();
        InputText maxPrice = table.filters().fields().field("Максимальная цена").control(InputText.class);
        maxPrice.shouldExists();

        minPrice.click();
        minPrice.setValue("160000");
        minPrice.shouldHaveValue("160000");
        table.filters().toolbar().button("Найти").click();
        table.columns().rows().shouldHaveSize(1);
        table.columns().rows().row(0).cell(2).shouldHaveText("161 000");
        table.filters().toolbar().button("Сбрость").click();
        minPrice.shouldBeEmpty();

        maxPrice.click();
        maxPrice.setValue("22000");
        maxPrice.shouldHaveValue("22000");
        table.filters().toolbar().button("Найти").click();
        table.columns().rows().shouldHaveSize(1);
        table.columns().rows().row(0).cell(2).shouldHaveText("21 000");

        maxPrice.click();
        maxPrice.setValue("34000");
        maxPrice.shouldHaveValue("34000");
        minPrice.click();
        minPrice.setValue("30000");
        minPrice.shouldHaveValue("30000");
        table.filters().toolbar().button("Найти").click();
        table.columns().rows().shouldHaveSize(1);
        table.columns().rows().row(0).cell(2).shouldHaveText("32 000");

        table.filters().toolbar().button("Сбрость").click();
        minPrice.shouldBeEmpty();
        maxPrice.shouldBeEmpty();
    }

}
