package net.n2oapp.framework.config.metadata.compile.widget;

import net.n2oapp.framework.api.metadata.N2oAbstractDatasource;
import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oTree;
import net.n2oapp.framework.api.metadata.global.view.widget.table.ShapeType;
import net.n2oapp.framework.api.metadata.local.CompiledObject;
import net.n2oapp.framework.api.metadata.meta.badge.BadgePresence;
import net.n2oapp.framework.api.metadata.meta.badge.Position;
import net.n2oapp.framework.api.metadata.meta.widget.Tree;
import org.springframework.stereotype.Component;

import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.property;

/**
 * Компиляция виджета дерево
 */
@Component
public class TreeCompiler extends BaseWidgetCompiler<Tree, N2oTree> {
    @Override
    protected String getPropertyWidgetSrc() {
        return "n2o.api.widget.tree.src";
    }

    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oTree.class;
    }

    @Override
    public Tree compile(N2oTree source, CompileContext<?, ?> context, CompileProcessor p) {
        Tree tree = new Tree();
        N2oAbstractDatasource datasource = initDatasource(tree, source, p);
        CompiledObject object = getObject(source, datasource, p);
        compileBaseWidget(tree, source, context, p, object);
        WidgetScope widgetScope = new WidgetScope(source.getId(), source.getDatasourceId(), ReduxModel.resolve, p);
        MetaActions widgetActions = initMetaActions(source, p);
        compileToolbarAndAction(tree, source, context, p, widgetScope, widgetActions, object, null);

        tree.setParentFieldId(p.resolveJS(source.getParentFieldId()));
        tree.setValueFieldId(p.resolveJS(source.getValueFieldId()));
        tree.setChildrenFieldId(p.resolveJS(source.getHasChildrenFieldId()));
        tree.setLabelFieldId(p.resolveJS(source.getLabelFieldId()));
        tree.setIconFieldId(p.resolveJS(source.getIconFieldId()));
        tree.setImageFieldId(p.resolveJS(source.getImageFieldId()));
        tree.setMultiselect(source.getMultiselect());
        tree.setHasCheckboxes(source.getCheckboxes());
        tree.setAjax(source.getAjax());
        tree.setBadge(BadgePresence.compileBadge(source,
                p.resolve(property("n2o.api.widget.tree.badge.position"), Position.class),
                p.resolve(property("n2o.api.widget.tree.badge.shape"), ShapeType.class),
                p.resolve(property("n2o.api.widget.tree.badge.image_position"), Position.class),
                p.resolve(property("n2o.api.widget.tree.badge.image_shape"), ShapeType.class),
                p));
        return tree;
    }

    @Override
    protected N2oAbstractDatasource initDatasource(Tree compiled, N2oTree source, CompileProcessor p) {
        N2oAbstractDatasource datasource = super.initDatasource(compiled, source, p);
        if (datasource.getSize() == null)
            datasource.setSize(p.resolve(property("n2o.api.widget.tree.size"), Integer.class));
        return datasource;
    }
}
