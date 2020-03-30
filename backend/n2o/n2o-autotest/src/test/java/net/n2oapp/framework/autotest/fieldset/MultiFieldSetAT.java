package net.n2oapp.framework.autotest.fieldset;

import net.n2oapp.framework.autotest.api.component.control.InputText;
import net.n2oapp.framework.autotest.api.component.fieldset.MultiFieldSet;
import net.n2oapp.framework.autotest.api.component.fieldset.MultiFieldSetItem;
import net.n2oapp.framework.autotest.api.component.page.SimplePage;
import net.n2oapp.framework.autotest.api.component.widget.FormWidget;
import net.n2oapp.framework.autotest.run.AutoTestBase;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.pack.*;
import net.n2oapp.framework.config.selective.CompileInfo;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Автотест для филдсета с динамическим числом полей
 */
public class MultiFieldSetAT extends AutoTestBase {

    private SimplePage page;

    @BeforeClass
    public static void beforeClass() {
        configureSelenide();
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oPagesPack(), new N2oHeaderPack(), new N2oWidgetsPack(), new N2oFieldSetsPack(),
                new N2oControlsPack(), new N2oObjectsPack());
    }

    @Test
    public void testAdd() {
        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/fieldset/multiset/add/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/blank.header.xml"));

        page = open(SimplePage.class);
        page.shouldExists();

        // 1.проверка, что при can-add="false" (нельзя добавить элемент)
        MultiFieldSet fieldset1 = page.single().widget(FormWidget.class).fieldsets().fieldset(0, MultiFieldSet.class);
        fieldset1.shouldExists();
        fieldset1.shouldBeEmpty();
        fieldset1.addButtonShouldNotBeExist();

        // 2.стандартный случай
        MultiFieldSet fieldset2 = page.single().widget(FormWidget.class).fieldsets().fieldset(1, MultiFieldSet.class);
        fieldset2.shouldExists();
        fieldset2.addButtonShouldBeExist();
        fieldset2.addButtonShouldHaveLabel("Добавить участника");
        fieldset2.clickAddButton();
        fieldset2.clickAddButton();
        fieldset2.shouldHaveItems(2);
        MultiFieldSetItem item1 = fieldset2.item(0);
        MultiFieldSetItem item2 = fieldset2.item(1);
        item1.shouldHaveLabel("Участник 1");
        item2.shouldHaveLabel("Участник 2");
        // проверяем корректность набора полей
        InputText name1 = item1.fields().field("name").control(InputText.class);
        name1.shouldExists();
        InputText age1 = item1.fields().field("age").control(InputText.class);
        age1.shouldExists();
        InputText name2 = item2.fields().field("name").control(InputText.class);
        name2.shouldExists();
        InputText age2 = item2.fields().field("age").control(InputText.class);
        age2.shouldExists();
        // задаем значения для полей первого элемента
        name1.val("Joe");
        name1.shouldHaveValue("Joe");
        age1.val("15");
        age1.shouldHaveValue("15");
        // проверяем, что значения не копируются в поля второго элемента
        name2.shouldBeEmpty();
        age2.shouldBeEmpty();
    }

    @Test
    public void testRemove() {
        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/fieldset/multiset/remove/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/blank.header.xml"));

        page = open(SimplePage.class);
        page.shouldExists();

        // 1.проверка при can-remove="false" (кнопок удаления нет)
        MultiFieldSet fieldset1 = page.single().widget(FormWidget.class).fieldsets().fieldset(0, MultiFieldSet.class);
        fieldset1.clickAddButton();
        fieldset1.clickAddButton();
        fieldset1.removeAllButtonShouldNotBeExist();
        MultiFieldSetItem item1 = fieldset1.item(0);
        MultiFieldSetItem item2 = fieldset1.item(1);
        item1.removeButtonShouldNotExists();
        item2.removeButtonShouldNotExists();

        // 2.стандартный случай
        MultiFieldSet fieldset2 = page.single().widget(FormWidget.class).fieldsets().fieldset(1, MultiFieldSet.class);
        fieldset2.clickAddButton();
        fieldset2.clickAddButton();
        fieldset2.clickAddButton();
        fieldset2.shouldHaveItems(3);
        item1 = fieldset2.item(0);
        item2 = fieldset2.item(1);
        MultiFieldSetItem item3 = fieldset2.item(2);
        InputText name1 = item1.fields().field("name").control(InputText.class);
        InputText name2 = item2.fields().field("name").control(InputText.class);
        InputText name3 = item3.fields().field("name").control(InputText.class);
        // проверяем наличие кнопок удалить у всех кроме первого элемента
        item1.removeButtonShouldNotExists();
        item2.removeButtonShouldExists();
        item3.removeButtonShouldExists();
        // задаем значения чтобы различать элементы
        name1.val("A");
        name2.val("B");
        name3.val("C");
        // проверяем, что при удалении второго у третьего изменится подпись
        item2.clickRemoveButton();
        fieldset2.shouldHaveItems(2);
        item1.shouldHaveLabel("Участник 1");
        item2.shouldHaveLabel("Участник 2");
        name1.shouldHaveValue("A");
        name2.shouldHaveValue("C");

        // 3.проверка при can-remove-all="true" (удаление всех элементов кроме первого)
        MultiFieldSet fieldset3 = page.single().widget(FormWidget.class).fieldsets().fieldset(2, MultiFieldSet.class);
        fieldset3.clickAddButton();
        fieldset3.clickAddButton();
        fieldset3.clickAddButton();
        fieldset3.shouldHaveItems(3);
        // задаем значение только у первого элемента
        name1 = fieldset3.item(0).fields().field("name").control(InputText.class);
        name1.val("A");
        // проверяем кнопку удалить всех
        fieldset3.removeAllButtonShouldBeExist();
        fieldset3.removeAllButtonShouldHaveLabel("Удалить всех участников");
        fieldset3.clickRemoveAllButton();
        fieldset3.shouldHaveItems(1);
        name1.shouldHaveValue("A");

        // 4.проверка при can-remove-all="true" can-remove-first="true" (удаление всех элементов)
        MultiFieldSet fieldset4 = page.single().widget(FormWidget.class).fieldsets().fieldset(3, MultiFieldSet.class);
        fieldset4.clickAddButton();
        fieldset4.clickAddButton();
        fieldset4.clickAddButton();
        fieldset4.shouldHaveItems(3);
        fieldset4.clickRemoveAllButton();
        fieldset4.shouldBeEmpty();
    }

    @Test
    public void testCopy() {
        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/fieldset/multiset/copy/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/blank.header.xml"));

        page = open(SimplePage.class);
        page.shouldExists();

        // 1.стандартный случай (нет кнопки копирования)
        MultiFieldSet fieldset1 = page.single().widget(FormWidget.class).fieldsets().fieldset(0, MultiFieldSet.class);
        fieldset1.clickAddButton();
        fieldset1.clickAddButton();
        MultiFieldSetItem item1 = fieldset1.item(0);
        MultiFieldSetItem item2 = fieldset1.item(1);
        item1.copyButtonShouldNotExists();
        item2.copyButtonShouldNotExists();

        // 2.копирование включено
        MultiFieldSet fieldset2 = page.single().widget(FormWidget.class).fieldsets().fieldset(1, MultiFieldSet.class);
        fieldset2.clickAddButton();
        fieldset2.clickAddButton();
        fieldset2.shouldHaveItems(2);
        item1 = fieldset2.item(0);
        item2 = fieldset2.item(1);
        item1.copyButtonShouldExists();
        item2.copyButtonShouldExists();
        // копируем второй элемент
        InputText name1 = item1.fields().field("name").control(InputText.class);
        InputText name2 = item2.fields().field("name").control(InputText.class);
        name2.val("A");
        item2.clickCopyButton();
        fieldset2.shouldHaveItems(3);
        MultiFieldSetItem item3 = fieldset2.item(1);
        InputText name3 = item3.fields().field("name").control(InputText.class);
        name3.shouldHaveValue("A");
        // изменяем значение второго элемента и удаляем
        name2.val("B");
        item2.clickRemoveButton();
        fieldset2.shouldHaveItems(2);
        // проверяем значение третьего элемента, который стал вторым
        name2.shouldHaveValue("A");
    }

    @Test
    public void testDependencies() {
        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/fieldset/multiset/dependencies/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/blank.header.xml"));

        page = open(SimplePage.class);
        page.shouldExists();

        MultiFieldSet fieldset1 = page.single().widget(FormWidget.class).fieldsets().fieldset(0, MultiFieldSet.class);
        fieldset1.clickAddButton();
        fieldset1.clickAddButton();
        MultiFieldSetItem item1 = fieldset1.item(0);
        MultiFieldSetItem item2 = fieldset1.item(1);
        InputText name1 = item1.fields().field("name").control(InputText.class);
        InputText age1 = item1.fields().field("age").control(InputText.class);
        InputText name2 = item2.fields().field("name").control(InputText.class);
        InputText age2 = item2.fields().field("age").control(InputText.class);
        name1.shouldBeDisabled();
        name2.shouldBeDisabled();
        age1.val("2");
        name1.shouldBeDisabled();
        name2.shouldBeDisabled();
        age2.val("20");
        name1.shouldBeDisabled();
        name2.shouldBeEnabled();
        age1.val("50");
        name1.shouldBeEnabled();
        name2.shouldBeEnabled();
        age2.val("15");
        name1.shouldBeEnabled();
        name2.shouldBeDisabled();
    }

    @Test
    public void testValidations() {
        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/fieldset/multiset/validations/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/fieldset/multiset/validations/test.object.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/blank.header.xml"));

        page = open(SimplePage.class);
        page.shouldExists();

        MultiFieldSet fieldset1 = page.single().widget(FormWidget.class).fieldsets().fieldset(0, MultiFieldSet.class);
        fieldset1.clickAddButton();
        fieldset1.clickAddButton();
        MultiFieldSetItem item1 = fieldset1.item(0);
        MultiFieldSetItem item2 = fieldset1.item(1);
        InputText age1 = item1.fields().field("age").control(InputText.class);
        InputText age2 = item2.fields().field("age").control(InputText.class);
        /// TODO message check
        age1.val("2");
        age1.val("20");
        age2.val("5");
        age2.val("50");
    }

    @Test
    public void testNestedMultiFieldSet() {
        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/fieldset/multiset/nested/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/blank.header.xml"));

        page = open(SimplePage.class);
        page.shouldExists();

        MultiFieldSet fieldset1 = page.single().widget(FormWidget.class).fieldsets().fieldset(0, MultiFieldSet.class);
        fieldset1.clickAddButton();
        MultiFieldSetItem item = fieldset1.item(0);
        item.fields().shouldHaveSize(1);
        MultiFieldSet fieldset2 = item.fieldsets().fieldset(0, MultiFieldSet.class);
        fieldset2.shouldExists();
        // проверяем функционал вложенного мультифилдсета
        // add
        fieldset2.addButtonShouldBeExist();
        fieldset2.addButtonShouldHaveLabel("Добавить участника");
        fieldset2.clickAddButton();
        fieldset2.clickAddButton();
        MultiFieldSetItem item1 = fieldset2.item(0);
        MultiFieldSetItem item2 = fieldset2.item(1);
        item1.shouldHaveLabel("Участник 1");
        item2.shouldHaveLabel("Участник 2");
        item1.removeButtonShouldNotExists();
        item1.copyButtonShouldExists();
        item2.removeButtonShouldExists();
        item2.copyButtonShouldExists();
        InputText name2 = item2.fields().field("name2").control(InputText.class);
        // copy
        name2.val("A");
        item2.copyButtonShouldExists();
        item2.clickCopyButton();
        fieldset2.shouldHaveItems(3);
        MultiFieldSetItem item3 = fieldset2.item(2);
        InputText name3 = item3.fields().field("name2").control(InputText.class);
        name3.shouldHaveValue("A");
        // remove
        fieldset2.clickAddButton();
        fieldset2.shouldHaveItems(4);
        item2.clickRemoveButton();
        fieldset2.shouldHaveItems(3);
        fieldset2.removeAllButtonShouldBeExist();
        fieldset2.removeAllButtonShouldHaveLabel("Удалить всех участников");
        fieldset2.clickRemoveAllButton();
        fieldset2.shouldHaveItems(1);
    }
}
