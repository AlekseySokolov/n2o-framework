package net.n2oapp.framework.config.metadata.compile.region;

import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.global.view.region.N2oScrollspyRegion;
import net.n2oapp.framework.api.metadata.meta.region.scrollspy.*;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static net.n2oapp.framework.api.StringUtils.prepareSizeAttribute;
import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.property;

/**
 * Компиляция региона с отслеживанием прокрутки
 */
@Component
public class ScrollspyRegionCompiler extends BaseRegionCompiler<ScrollspyRegion, N2oScrollspyRegion> {

    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oScrollspyRegion.class;
    }

    @Override
    protected String createId(CompileProcessor p) {
        return createId("scrollspy", p);
    }

    @Override
    protected String getSrcProperty() {
        return "n2o.api.region.scrollspy.src";
    }

    @Override
    public ScrollspyRegion compile(N2oScrollspyRegion source, PageContext context, CompileProcessor p) {
        ScrollspyRegion region = new ScrollspyRegion();
        build(region, source, p);
        region.setTitle(source.getTitle());
        region.setPlacement(p.cast(source.getPlacement(),
                () -> p.resolve(property("n2o.api.region.scrollspy.placement"), String.class)));
        region.setHeadlines(p.cast(source.getHeadlines(),
                () -> p.resolve(property("n2o.api.region.scrollspy.headlines"), Boolean.class)));
        region.setMaxHeight(prepareSizeAttribute(source.getMaxHeight()));
        region.setMenu(initMenu(source.getMenu(), context, p));
        region.setActive(source.getActive());
        compileRoute(source, region.getId(), "n2o.api.region.scrollspy.routable", p);
        return region;
    }

    private List<ScrollspyElement> initMenu(N2oScrollspyRegion.AbstractMenuItem[] items, PageContext context, CompileProcessor p) {
        if (items == null || items.length == 0)
            return null;
        List<ScrollspyElement> elements = new ArrayList<>();
        for (N2oScrollspyRegion.AbstractMenuItem item : items) {
            if (item instanceof N2oScrollspyRegion.MenuItem) {
                elements.add(initSingleElement(item, context, p));
            } else if (item instanceof N2oScrollspyRegion.SubMenuItem) {
                elements.add(initSubElement(item, context, p));
            } else if (item instanceof N2oScrollspyRegion.GroupItem) {
                elements.add(initGroupElement(item, context, p));
            }
        }
        return elements;
    }

    private SingleScrollspyElement initSingleElement(N2oScrollspyRegion.AbstractMenuItem item, PageContext context, CompileProcessor p) {
        SingleScrollspyElement element = new SingleScrollspyElement();
        initElement(element, item, p);
        element.setContent(initContent(((N2oScrollspyRegion.MenuItem) item).getContent(), context, p, item));
        return element;
    }

    private MenuScrollspyElement initSubElement(N2oScrollspyRegion.AbstractMenuItem item, PageContext context, CompileProcessor p) {
        MenuScrollspyElement element = new MenuScrollspyElement();
        initElement(element, item, p);
        element.setMenu(initMenu(((N2oScrollspyRegion.SubMenuItem) item).getSubMenu(), context, p));
        return element;
    }

    private GroupScrollspyElement initGroupElement(N2oScrollspyRegion.AbstractMenuItem item, PageContext context, CompileProcessor p) {
        GroupScrollspyElement element = new GroupScrollspyElement();
        initElement(element, item, p);
        element.setGroup(initMenu(((N2oScrollspyRegion.GroupItem) item).getGroup(), context, p));
        element.setHeadline(p.cast(((N2oScrollspyRegion.GroupItem) item).getHeadline(),
                () -> p.resolve(property("n2o.api.region.scrollspy.group.headline"), Boolean.class)));
        return element;
    }

    private void initElement(ScrollspyElement element, N2oScrollspyRegion.AbstractMenuItem item, CompileProcessor p) {
        element.setId(p.cast(item.getId(), createId("element_scrollspy", p)));
        element.setTitle(item.getTitle());
    }
}
