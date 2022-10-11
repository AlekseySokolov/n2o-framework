package net.n2oapp.framework.config.metadata.compile.toolbar;

import net.n2oapp.framework.api.StringUtils;
import net.n2oapp.framework.api.exception.N2oException;
import net.n2oapp.framework.api.metadata.N2oAbstractDatasource;
import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.compile.building.Placeholders;
import net.n2oapp.framework.api.metadata.event.action.N2oAction;
import net.n2oapp.framework.api.metadata.global.view.page.datasource.N2oStandardDatasource;
import net.n2oapp.framework.api.metadata.global.view.widget.table.column.cell.N2oCell;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.Confirm;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.ConfirmType;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.DisableOnEmptyModelType;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.N2oButton;
import net.n2oapp.framework.api.metadata.local.CompiledObject;
import net.n2oapp.framework.api.metadata.local.CompiledQuery;
import net.n2oapp.framework.api.metadata.meta.ModelLink;
import net.n2oapp.framework.api.metadata.meta.action.Action;
import net.n2oapp.framework.api.metadata.meta.action.invoke.InvokeAction;
import net.n2oapp.framework.api.metadata.meta.control.ValidationType;
import net.n2oapp.framework.api.metadata.meta.widget.toolbar.Condition;
import net.n2oapp.framework.api.metadata.meta.widget.toolbar.PerformButton;
import net.n2oapp.framework.api.script.ScriptProcessor;
import net.n2oapp.framework.config.metadata.compile.ComponentScope;
import net.n2oapp.framework.config.metadata.compile.IndexScope;
import net.n2oapp.framework.config.metadata.compile.context.ObjectContext;
import net.n2oapp.framework.config.metadata.compile.context.QueryContext;
import net.n2oapp.framework.config.metadata.compile.datasource.DataSourcesScope;
import net.n2oapp.framework.config.metadata.compile.widget.MetaActions;
import net.n2oapp.framework.config.metadata.compile.widget.WidgetScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.n2oapp.framework.api.StringUtils.isLink;
import static net.n2oapp.framework.api.StringUtils.unwrapLink;
import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.*;
import static net.n2oapp.framework.config.util.DatasourceUtil.getClientDatasourceId;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

/**
 * Компиляция кнопки
 */
@Component
public class PerformButtonCompiler extends BaseButtonCompiler<N2oButton, PerformButton> {
    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oButton.class;
    }

    @Override
    public PerformButton compile(N2oButton source, CompileContext<?, ?> context, CompileProcessor p) {
        initDefaults(source, context, p);
        PerformButton button = new PerformButton();
        IndexScope idx = p.getScope(IndexScope.class);
        compileBase(button, source, idx, context, p);
        button.setSrc(source.getSrc());
        button.setRounded(source.getRounded());
        button.setValidate(compileValidate(source, p));
        CompiledObject compiledObject = initObject(p, source);
        Action action = compileAction(source, context, p, compiledObject);
        //button.setAction(action);
        compileLink(button);
        button.setConfirm(compileConfirm(source, action, p, compiledObject));
        compileDependencies(source, button, p);
        return button;
    }

    private CompiledObject.Operation getOperation(Action action, CompiledObject compiledObject) {
        CompiledObject.Operation operation = null;
        if (action != null) {
            if (action instanceof InvokeAction) {

                operation = compiledObject != null && compiledObject.getOperations() != null
                        && compiledObject.getOperations().containsKey(((InvokeAction) action).getOperationId()) ?
                        compiledObject.getOperations().get(((InvokeAction) action).getOperationId()) : null;
            }
            //todo если это invoke-action, то из action в объекте должны доставаться поля action.getName(), confirmationText
        }
        return operation;
    }

    protected void initDefaults(N2oButton source, CompileContext<?, ?> context, CompileProcessor p) {
        source.setId(p.cast(source.getId(), isNotEmpty(source.getActionIds()) ? String.join("_", source.getActionIds()) : null));
        super.initDefaults(source, context, p);
        source.setSrc(p.cast(source.getSrc(), p.resolve(property("n2o.api.action.button.src"), String.class)));
        source.setRounded(p.cast(source.getRounded(), false));
        String datasource = initDatasource(source, p);
        source.setDatasourceId(datasource);
        boolean validate = initValidate(source, p, datasource);
        source.setValidate(validate);
        source.setModel(p.cast(source.getModel(), ReduxModel.resolve));
        source.setValidateDatasourceIds(initValidateDatasources(source, validate, datasource));
        source.setActions(initActions(source, p));
    }

    private Boolean initValidate(N2oButton source, CompileProcessor p, String datasource) {
        if (isEmpty(source.getActionIds()))
            return p.cast(source.getValidate(), false);
        return p.cast(source.getValidate(), datasource != null || source.getValidateDatasourceIds() != null);
    }

    private N2oAction[] initActions(N2oButton source, CompileProcessor p) {
        if (source.getActions() != null)
            return source.getActions();

        if (isNotEmpty(source.getActionIds())) {
            MetaActions metaActions = p.getScope(MetaActions.class);
            List<N2oAction> actions = new ArrayList<>();
            for (String actionId : source.getActionIds()) {
                if (metaActions.containsKey(actionId) && metaActions.get(actionId).getAction() != null)
                    actions.add(metaActions.get(actionId).getAction());
            }
            return actions.toArray(new N2oAction[0]);
        }
        return null;
    }

    private List<String> compileValidate(N2oButton source, CompileProcessor p) {
        if (!Boolean.TRUE.equals(source.getValidate()))
            return null;
        if (source.getValidateDatasourceIds() == null || source.getValidateDatasourceIds().length == 0)
            throw new N2oException(String.format("validate-datasources is not defined in button [%s]", source.getId()));
        return Stream.of(source.getValidateDatasourceIds())
                .map(ds -> getClientDatasourceId(ds, p))
                .collect(Collectors.toList());
    }

    private String[] initValidateDatasources(N2oButton source, boolean validate, String datasource) {
        if (validate) {
            if (source.getValidateDatasourceIds() != null)
                return source.getValidateDatasourceIds();
            if (source.getDatasourceId() != null)
                return new String[]{datasource};
        }
        return null;
    }

    private CompiledObject initObject(CompileProcessor p, N2oButton button) {
        if (button.getDatasourceId() != null && p.getScope(DataSourcesScope.class) != null) {
            N2oAbstractDatasource datasource = p.getScope(DataSourcesScope.class).get(button.getDatasourceId());
            if (datasource instanceof N2oStandardDatasource) {
                N2oStandardDatasource standardDatasource = (N2oStandardDatasource) datasource;
                if (standardDatasource.getObjectId() != null) {
                    return p.getCompiled(new ObjectContext(standardDatasource.getObjectId()));
                } else if (standardDatasource.getQueryId() != null) {
                    CompiledQuery query = p.getCompiled(new QueryContext(standardDatasource.getQueryId()));
                    return query.getObject();
                }
            }
        }
        return p.getScope(CompiledObject.class);
    }

    private void compileLink(PerformButton button) {
//        if (button.getAction() instanceof LinkAction) {
//            LinkAction linkAction = ((LinkAction) button.getAction());
//            button.setUrl(linkAction.getUrl());
//            button.setTarget(linkAction.getTarget());
//            if (linkAction.getPathMapping() != null)
//                button.setPathMapping(new StrictMap<>(linkAction.getPathMapping()));
//            if (linkAction.getQueryMapping() != null)
//                button.setQueryMapping(new StrictMap<>(linkAction.getQueryMapping()));
//        }
    }

    private Confirm compileConfirm(N2oButton source,
                                   Action action,
                                   CompileProcessor p, CompiledObject object) {
        CompiledObject.Operation operation = getOperation(action, object);
        boolean operationConfirm = operation != null && operation.getConfirm() != null && operation.getConfirm();
        if (source.getConfirm() != null) {
            Object condition = p.resolveJS(source.getConfirm(), Boolean.class);
            if (condition instanceof Boolean) {
                if (!((Boolean) condition || operationConfirm))
                    return null;
                return initConfirm(source, p, operation, true);
            }
            if (condition instanceof String) {
                return initConfirm(source, p, operation, condition);
            }
        }
        if (operationConfirm)
            return initConfirm(source, p, operation, true);
        return null;
    }

    private Confirm initConfirm(N2oButton source, CompileProcessor p, CompiledObject.Operation operation, Object condition) {
        Confirm confirm = new Confirm();
        confirm.setMode(p.cast(source.getConfirmType(), ConfirmType.modal));
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

        if (StringUtils.isJs(confirm.getText()) || StringUtils.isJs(confirm.getCondition())) {
            String clientDatasource = getClientDatasourceId(source.getDatasourceId(), p);
            confirm.setModelLink(new ModelLink(source.getModel(), clientDatasource).getBindLink());
        }
        return confirm;
    }

    private String initConfirmCondition(Object condition) {
        if (condition instanceof Boolean)
            return Placeholders.js(Boolean.toString(true));
        return initExpression((String) condition);
    }

    private String initExpression(String attr) {
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

    private Action compileAction(N2oButton source, CompileContext<?, ?> context, CompileProcessor p, CompiledObject object) {
//        N2oAction butAction = source.getAction();FIXME
//        if (source.getAction() == null && source.getActionId() != null) {
//            MetaActions metaActions = p.getScope(MetaActions.class);
//            butAction = metaActions.get(source.getActionId()) == null ? null : metaActions.get(source.getActionId()).getAction();
//        }
//        if (butAction == null) return null;
//        butAction.setId(p.cast(butAction.getId(), source.getId()));
//        return p.compile(butAction, context, object, new ComponentScope(source, p.getScope(ComponentScope.class)));
        return null;
    }

    private String initDatasource(N2oButton source, CompileProcessor p) {
        if (source.getDatasourceId() != null)
            return source.getDatasourceId();
        WidgetScope widgetScope = p.getScope(WidgetScope.class);
        if (widgetScope != null)
            return widgetScope.getDatasourceId();
        return null;
    }

    /**
     * Компиляция условий и зависимостей кнопки
     *
     * @param button Клиентская модель кнопки
     * @param source Исходная модель кнопки
     * @param p      Процессор сборки метаданных
     */
    protected void compileDependencies(N2oButton source, PerformButton button,
                                       CompileProcessor p) {
        String clientDatasource = getClientDatasourceId(source.getDatasourceId(), p);
        List<Condition> enabledConditions = new ArrayList<>();

        if (source.getVisibilityCondition() != null)
            button.getConditions().put(ValidationType.visible,
                    compileCondition(source.getVisibilityCondition(), source.getModel(), clientDatasource));
        if (source.getEnablingCondition() != null)
            enabledConditions.addAll(compileCondition(source.getEnablingCondition(), source.getModel(), clientDatasource));

        ComponentScope componentScope = p.getScope(ComponentScope.class);

        if (source.getDatasourceId() != null) {
            Condition emptyModelCondition = enabledByEmptyModelCondition(source, clientDatasource, componentScope, p);
            if (emptyModelCondition != null)
                enabledConditions.add(emptyModelCondition);
        }

        if (!enabledConditions.isEmpty()) {
            button.getConditions().put(ValidationType.enabled, enabledConditions);
        }

        if (source.getDependencies() != null)
            compileDependencies(source.getDependencies(), button, clientDatasource, source.getModel(), p);

        if (componentScope != null && componentScope.unwrap(N2oCell.class) != null) {
            button.setVisible(p.resolveJS(source.getVisible(), Boolean.class));
            button.setEnabled(p.resolveJS(source.getEnabled(), Boolean.class));
        } else {
            if (isLink(source.getVisible()))
                compileLinkCondition(button, clientDatasource, ValidationType.visible, source.getVisible(), source.getModel());
            else
                button.setVisible(p.resolveJS(source.getVisible(), Boolean.class));

            if (isLink(source.getEnabled()))
                compileLinkCondition(button, clientDatasource, ValidationType.enabled, source.getEnabled(), source.getModel());
            else
                button.setEnabled(p.resolveJS(source.getEnabled(), Boolean.class));
        }
    }

    /**
     * Получение условия доступности кнопки при пустой модели
     *
     * @param source           Исходная модель кнопки
     * @param clientDatasource Идентификатор источника данных, к которому относится кнопка
     * @param componentScope   Родительский компонент
     * @param p                Процессор сборки метаданных
     * @return Условие доступности кнопки при пустой модели
     */
    private Condition enabledByEmptyModelCondition(N2oButton source, String clientDatasource, ComponentScope componentScope, CompileProcessor p) {
        DisableOnEmptyModelType disableOnEmptyModel = p.cast(source.getDisableOnEmptyModel(),
                p.resolve(property("n2o.api.button.disable_on_empty_model"), DisableOnEmptyModelType.class));
        if (DisableOnEmptyModelType.FALSE.equals(disableOnEmptyModel)) return null;

        boolean parentIsNotCell = componentScope == null || componentScope.unwrap(N2oCell.class) == null;
        boolean autoDisableCondition = DisableOnEmptyModelType.AUTO.equals(disableOnEmptyModel) &&
                (ReduxModel.resolve.equals(source.getModel()) || ReduxModel.multi.equals(source.getModel())) &&
                parentIsNotCell;

        if (DisableOnEmptyModelType.TRUE.equals(disableOnEmptyModel) || autoDisableCondition) {
            Condition condition = new Condition();
            condition.setExpression("!$.isEmptyModel(this)");
            condition.setModelLink(new ModelLink(source.getModel(), clientDatasource).getBindLink());
            return condition;
        }
        return null;
    }

    private List<Condition> compileCondition(String expression, ReduxModel model, String clientDatasource) {
        List<Condition> result = new ArrayList<>();
        Condition condition = new Condition();
        condition.setExpression(expression.trim());
        condition.setModelLink(new ModelLink(model, clientDatasource).getBindLink());
        result.add(condition);
        return result;
    }

    private void compileLinkCondition(PerformButton button, String clientDatasource, ValidationType type,
                                      String linkCondition, ReduxModel model) {
        Condition condition = new Condition();
        condition.setExpression(unwrapLink(linkCondition));
        condition.setModelLink(new ModelLink(model, clientDatasource).getBindLink());
        if (!button.getConditions().containsKey(type))
            button.getConditions().put(type, new ArrayList<>());
        button.getConditions().get(type).add(condition);
    }

    private void compileDependencies(N2oButton.Dependency[] dependencies, PerformButton button, String clientDatasource,
                                     ReduxModel buttonModel, CompileProcessor p) {
        for (N2oButton.Dependency d : dependencies) {
            ValidationType validationType = null;
            if (d instanceof N2oButton.EnablingDependency)
                validationType = ValidationType.enabled;
            else if (d instanceof N2oButton.VisibilityDependency)
                validationType = ValidationType.visible;

            compileCondition(d, button, validationType, clientDatasource, buttonModel, p);
        }
    }

    private void compileCondition(N2oButton.Dependency dependency, PerformButton button, ValidationType validationType,
                                  String buttonDatasource, ReduxModel buttonModel, CompileProcessor p) {
        ReduxModel refModel = p.cast(dependency.getModel(), buttonModel, ReduxModel.resolve);
        Condition condition = new Condition();
        condition.setExpression(ScriptProcessor.resolveFunction(dependency.getValue()));
        String datasource = (dependency.getDatasource() != null) ?
                getClientDatasourceId(dependency.getDatasource(), p) :
                buttonDatasource;
        condition.setModelLink(new ModelLink(refModel, datasource, null).getBindLink());
        if (dependency instanceof N2oButton.EnablingDependency)
            condition.setMessage(((N2oButton.EnablingDependency) dependency).getMessage());

        if (!button.getConditions().containsKey(validationType))
            button.getConditions().put(validationType, new ArrayList<>());
        button.getConditions().get(validationType).add(condition);
    }
}
