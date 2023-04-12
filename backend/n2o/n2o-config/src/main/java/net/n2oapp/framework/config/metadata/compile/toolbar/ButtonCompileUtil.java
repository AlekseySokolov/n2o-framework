package net.n2oapp.framework.config.metadata.compile.toolbar;

import net.n2oapp.framework.api.StringUtils;
import net.n2oapp.framework.api.exception.N2oException;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.compile.building.Placeholders;
import net.n2oapp.framework.api.metadata.control.N2oButtonField;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.Button;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.Confirm;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.ConfirmType;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.N2oButton;
import net.n2oapp.framework.api.metadata.local.CompiledObject;
import net.n2oapp.framework.api.metadata.meta.ModelLink;
import net.n2oapp.framework.config.metadata.compile.control.ButtonFieldCompiler;
import net.n2oapp.framework.config.metadata.compile.widget.WidgetScope;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.js;
import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.property;
import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.ref;
import static net.n2oapp.framework.config.util.DatasourceUtil.getClientDatasourceId;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;

public class ButtonCompileUtil {

    public static Confirm compileConfirm(Button source,
                                   CompileProcessor p, CompiledObject.Operation operation, ButtonFieldCompiler compiler) {
        boolean operationConfirm = operation != null && operation.getConfirm() != null && operation.getConfirm();
        if (source.getConfirm() != null) {
            Object condition = p.resolveJS(source.getConfirm(), Boolean.class);
            if (condition instanceof Boolean) {
                if (!((Boolean) condition || operationConfirm))
                    return null;
                return initConfirm(source, p, operation, true, compiler);
            }
            if (condition instanceof String) {
                return initConfirm(source, p, operation, condition, compiler);
            }
        }
        if (operationConfirm)
            return initConfirm(source, p, operation, true, compiler);
        return null;
    }

    public static Confirm initConfirm(Button source, CompileProcessor p, CompiledObject.Operation operation, Object condition, ButtonFieldCompiler compiler) {
        Confirm confirm = new Confirm();
        confirm.setMode(p.cast(source.getConfirmType(), ConfirmType.MODAL));
        confirm.setTitle(p.cast(source.getConfirmTitle(), operation != null ? operation.getFormSubmitLabel() : null, p.getMessage("n2o.confirm.title")));
        confirm.setOk(new Confirm.Button(
                p.cast(source.getConfirmOkLabel(), p.getMessage("n2o.confirm.default.okLabel")),
                p.cast(source.getConfirmOkColor(), p.resolve(property("n2o.api.button.confirm.ok_color"), String.class))));
        confirm.setCancel(new Confirm.Button(
                p.cast(source.getConfirmCancelLabel(), p.getMessage("n2o.confirm.default.cancelLabel")),
                p.cast(source.getConfirmCancelColor(), p.resolve(property("n2o.api.button.confirm.cancel_color"), String.class))));
        confirm.setText(initExpression(
                p.cast(source.getConfirmText(), operation != null ? operation.getConfirmationText() : null, p.getMessage("n2o.confirm.text"))));
        confirm.setCondition(initConfirmCondition(condition));
        confirm.setCloseButton(p.resolve(property("n2o.api.button.confirm.close_button"), Boolean.class));
        confirm.setReverseButtons(p.resolve(property("n2o.api.button.confirm.reverse_buttons"), Boolean.class));

        if (source instanceof N2oButtonField && StringUtils.hasLink(confirm.getText())) {
            Set<String> links = StringUtils.collectLinks(confirm.getText());
            String text = Placeholders.js("'" + confirm.getText() + "'");
            for (String link : links) {
                text = text.replace(Placeholders.ref(link), "' + this." + link + " + '");
            }
            confirm.setText(text);
        }

        if (StringUtils.isJs(confirm.getText()) || StringUtils.isJs(confirm.getCondition())) {
            String clientDatasource = initClientDatasourceId(source, p, compiler);
            confirm.setModelLink(new ModelLink(source.getModel(), clientDatasource).getBindLink());
        }

        return confirm;
    }

    protected static String initClientDatasourceId(Button source, CompileProcessor p, ButtonFieldCompiler compiler) {
        if (source.getDatasourceId() != null)
            return getClientDatasourceId(source.getDatasourceId(), p);

        String datasourceId = compiler.executeInitLocalDatasourceId(p);
        if (datasourceId != null)
            return getClientDatasourceId(datasourceId, p);
        else
            throw new N2oException(String.format("Unknown datasource for submit in field %s!", ((N2oButtonField)source).getId()));
    }

    public static String initConfirmCondition(Object condition) {
        if (condition instanceof Boolean)
            return Placeholders.js(Boolean.toString(true));
        return initExpression((String) condition);
    }

    public static String initExpression(String attr) {
        if (StringUtils.hasLink(attr)) {
            Set<String> links = StringUtils.collectLinks(attr);
            String text = js("'" + attr + "'");
            for (String link : links) {
                text = text.replace(ref(link), "' + this." + link + " + '");
            }
            return text;
        }
        return attr;
    }

    public static String initDatasource(Button source, CompileProcessor p) {
        if (source.getDatasourceId() != null)
            return source.getDatasourceId();
        WidgetScope widgetScope = p.getScope(WidgetScope.class);
        if (widgetScope != null)
            return widgetScope.getDatasourceId();
        return null;
    }

    public static List<String> compileValidate(Button source, CompileProcessor p, String datasource) {
        if (!Boolean.TRUE.equals(source.getValidate()))
            return null;
        if (source.getValidateDatasourceIds() != null && source.getValidateDatasourceIds().length != 0)
            return Stream.of(source.getValidateDatasourceIds())
                    .map(ds -> getClientDatasourceId(ds, p))
                    .collect(Collectors.toList());
        if (datasource != null)
            return Collections.singletonList(getClientDatasourceId(datasource, p));

        if (source instanceof N2oButton)
            throw new N2oException(String.format("validate-datasources is not defined in button [%s]", ((N2oButton)source).getId()));
        throw new N2oException(String.format("validate-datasources is not defined in button [%s]", ((N2oButtonField)source).getId()));
    }

    public static Boolean initValidate(Button source, CompileProcessor p, String datasource) {
        if (isEmpty(source.getActions()))
            return p.cast(source.getValidate(), false);
        return p.cast(source.getValidate(), datasource != null || source.getValidateDatasourceIds() != null);
    }
}
