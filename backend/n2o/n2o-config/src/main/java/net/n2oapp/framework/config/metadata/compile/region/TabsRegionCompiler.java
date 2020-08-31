package net.n2oapp.framework.config.metadata.compile.region;

import net.n2oapp.framework.api.metadata.Compiled;
import net.n2oapp.framework.api.metadata.SourceComponent;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.global.view.region.N2oRegion;
import net.n2oapp.framework.api.metadata.global.view.region.N2oTabsRegion;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oWidget;
import net.n2oapp.framework.api.metadata.meta.page.PageRoutes;
import net.n2oapp.framework.api.metadata.meta.region.TabsRegion;
import net.n2oapp.framework.config.metadata.compile.IndexScope;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import net.n2oapp.framework.config.metadata.compile.redux.Redux;
import net.n2oapp.framework.config.metadata.compile.widget.PageWidgetsScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.property;

/**
 * Компиляция региона в виде вкладок
 */
@Component
public class TabsRegionCompiler extends BaseRegionCompiler<TabsRegion, N2oTabsRegion> {

    @Override
    protected String getPropertyRegionSrc() {
        return "n2o.api.region.tabs.src";
    }

    @Override
    public Class<N2oTabsRegion> getSourceClass() {
        return N2oTabsRegion.class;
    }

    @Override
    public TabsRegion compile(N2oTabsRegion source, PageContext context, CompileProcessor p) {
        TabsRegion region = new TabsRegion();
        build(region, source, p);
        IndexScope indexScope = p.getScope(IndexScope.class);
        PageWidgetsScope pageWidgetsScope = p.getScope(PageWidgetsScope.class);
        region.setItems(initItems(source, indexScope, pageWidgetsScope, context, p));
        region.setAlwaysRefresh(source.getAlwaysRefresh() != null ? source.getAlwaysRefresh() : false);
        region.setLazy(p.cast(source.getLazy(), p.resolve(property("n2o.api.region.tabs.lazy"), Boolean.class)));
        compileTabsRoute(source, region.getId(), p);
        return region;
    }

    private void compileTabsRoute(N2oTabsRegion source, String regionId, CompileProcessor p) {
        String activeParam = p.cast(source.getActiveParam(), regionId);
        Boolean routable = p.cast(source.getRoutable(), p.resolve(property("n2o.api.region.tabs.routable"), Boolean.class));

        PageRoutes routes = p.getScope(PageRoutes.class);
        if (routes == null || !Boolean.TRUE.equals(routable))
            return;

        routes.addQueryMapping(
                activeParam,
                Redux.dispatchSetActiveRegionEntity(regionId, activeParam),
                Redux.createActiveRegionEntityLink(regionId)
        );
    }

    @Override
    protected String createId(String regionPlace, CompileProcessor p) {
        return createId(regionPlace, "tab", p);
    }


    protected List<TabsRegion.Tab> initItems(N2oTabsRegion source, IndexScope index, PageWidgetsScope pageWidgetsScope,
                                             PageContext context, CompileProcessor p) {
        List<TabsRegion.Tab> items = new ArrayList<>();
        if (source.getTabs() != null)
            for (N2oTabsRegion.Tab t : source.getTabs()) {
                TabsRegion.Tab tab = new TabsRegion.Tab();
                tab.setId(createId(null, source.getAlias(), p));
                tab.setLabel(t.getName());
                List<Compiled> content = new ArrayList<>();
                if (t.getItems() != null)
                    for (SourceComponent item : t.getItems())
                        if (item instanceof N2oWidget)
                            pageWidgetsScope.getWidgets().keySet().stream()
                                    .filter(k -> k.endsWith(((N2oWidget) item).getId())).findFirst()
                                    .ifPresent(s -> content.add(pageWidgetsScope.getWidgets().get(s)));
                        else if (item instanceof N2oRegion)
                            content.add(p.compile(item, context, p, index));
                tab.setContent(content);
                items.add(tab);
            }
        return items;
    }
}
