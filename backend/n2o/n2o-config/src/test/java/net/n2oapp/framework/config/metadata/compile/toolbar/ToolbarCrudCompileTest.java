package net.n2oapp.framework.config.metadata.compile.toolbar;

import net.n2oapp.framework.api.metadata.meta.action.close.CloseAction;
import net.n2oapp.framework.api.metadata.meta.action.close.CloseActionPayload;
import net.n2oapp.framework.api.metadata.meta.action.invoke.InvokeAction;
import net.n2oapp.framework.api.metadata.meta.action.modal.show_modal.ShowModal;
import net.n2oapp.framework.api.metadata.meta.page.Page;
import net.n2oapp.framework.api.metadata.meta.page.SimplePage;
import net.n2oapp.framework.api.metadata.meta.widget.form.Form;
import net.n2oapp.framework.api.metadata.meta.widget.toolbar.AbstractButton;
import net.n2oapp.framework.api.metadata.pipeline.ReadCompileBindTerminalPipeline;
import net.n2oapp.framework.api.metadata.pipeline.ReadCompileTerminalPipeline;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.compile.context.ModalPageContext;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import net.n2oapp.framework.config.metadata.pack.N2oAllDataPack;
import net.n2oapp.framework.config.metadata.pack.N2oAllPagesPack;
import net.n2oapp.framework.config.selective.CompileInfo;
import net.n2oapp.framework.config.test.SourceCompileTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ToolbarCrudCompileTest extends SourceCompileTestBase {
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oAllDataPack(), new N2oAllPagesPack());
        builder.sources(new CompileInfo("net/n2oapp/framework/config/metadata/compile/stub/utBlank.query.xml"),
                new CompileInfo("net/n2oapp/framework/config/metadata/compile/stub/utBlank.object.xml"),
                new CompileInfo("net/n2oapp/framework/config/metadata/compile/stub/utBlank.widget.xml"),
                new CompileInfo("net/n2oapp/framework/config/metadata/compile/stub/utBlank.page.xml"));
    }

    @Test
    public void testGeneratedCrudButtons() {
        ReadCompileTerminalPipeline<ReadCompileBindTerminalPipeline> pipeline = compile(
                "net/n2oapp/framework/config/metadata/compile/widgets/testToolbarCrudCompile.page.xml");
        SimplePage page = (SimplePage) pipeline.get(new PageContext("testToolbarCrudCompile"));
        Form form = (Form) page.getWidget();
        assertThat(((ModalPageContext) builder.route("/testToolbarCrudCompile/main/create", Page.class, null))
                .getPageName(), is("Пустой объект для unit тестов - Создание"));
        assertThat(((ModalPageContext) builder.route("/testToolbarCrudCompile/main/1/update", Page.class, null))
                .getPageName(), is("Пустой объект для unit тестов - Изменение"));

        assertThat(form.getToolbar().size(), is(2));
        assertThat(form.getToolbar().get("topLeft").get(0).getButtons().size(), is(4));
        assertThat(form.getToolbar().get("bottomLeft").get(0).getButtons().size(), is(1));
        List<String> buttonsId = form.getToolbar().get("topLeft").get(0)
                .getButtons().stream().map(AbstractButton::getId).collect(Collectors.toList());
        List<String> buttonsLabel = form.getToolbar().get("topLeft").get(0)
                .getButtons().stream().map(AbstractButton::getLabel).collect(Collectors.toList());
        List<String> buttonsAction = form.getToolbar().get("topLeft").get(0)
                .getButtons().stream().map(AbstractButton::getAction).map(a -> {
                    if (a instanceof ShowModal) return ((ShowModal) a).getOperationId();
                    if (a instanceof InvokeAction) return ((InvokeAction) a).getOperationId();
                    return null;
                }).collect(Collectors.toList());

        assertThat(buttonsId.contains("create"), is(true));
        assertThat(buttonsId.contains("update"), is(true));
        assertThat(buttonsId.contains("delete"), is(true));

        assertThat(buttonsAction.contains("create"), is(true));
        assertThat(buttonsAction.contains("update"), is(true));
        assertThat(buttonsAction.contains("delete"), is(true));

        assertThat(buttonsLabel.contains("Создать"), is(true));
        assertThat(buttonsLabel.contains("Изменить"), is(true));
        assertThat(buttonsLabel.contains("Удалить"), is(true));

        for (AbstractButton button : form.getToolbar().get("topLeft").get(0).getButtons()) {
            if ("delete".equalsIgnoreCase(button.getId())) {
                assertThat(button.getConfirm().getText(), is(builder.getEnvironment().getMessageSource().getMessage("n2o.confirm.text")));
                assertThat(button.getConfirm().getTitle(), is(builder.getEnvironment().getMessageSource().getMessage("n2o.confirm.title")));
                assertThat(button.getConfirm().getOk().getLabel(), is(builder.getEnvironment().getMessageSource().getMessage("n2o.confirm.default.okLabel")));
                assertThat(button.getConfirm().getCancel().getLabel(), is(builder.getEnvironment().getMessageSource().getMessage("n2o.confirm.default.cancelLabel")));
                assertThat(button.getConfirm().getCloseButton(), is(false));
            } else {
                assertThat(button.getConfirm(), nullValue());
            }
        }

        assertThat(form.getToolbar().get("bottomLeft").get(0).getButtons().get(0).getConfirm(), notNullValue());//action2
    }

    @Test
    public void testGenerationCloseModal() {
        PageContext context = new ModalPageContext("testCloseActionModal", "/test");
      //  context.setSubmitOperationId("update");
        //context.setRedirectUrlOnSuccessSubmit("/test/:id");
        //context.setRedirectTargetOnSuccessSubmit(Target.application);
        context.setRefreshOnClose(true);
        context.setParentRoute("/test");

        SimplePage page = (SimplePage) compile("net/n2oapp/framework/config/metadata/compile/action/testCloseActionModal.page.xml").get(context);
        CloseAction close = (CloseAction) page.getToolbar().getButton("close").getAction();

        assertThat(close.getType(), is("n2o/overlays/CLOSE"));
        assertThat(((CloseActionPayload) close.getPayload()).getPrompt(), is(true));
//        assertThat(close.getOptions().getMeta().getRedirect().getPath(), is("/test/:id"));
//        assertThat(close.getOptions().getMeta().getRedirect().getTarget(), is(RedirectTargetType.application));
//        assertThat(close.getOptions().getMeta().getRedirect().getPathMapping().get("id").getBindLink(), is("models.resolve['testCloseActionModal_update_main'].id"));

    }
}
