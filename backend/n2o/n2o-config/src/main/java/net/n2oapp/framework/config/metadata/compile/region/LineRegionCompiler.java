package net.n2oapp.framework.config.metadata.compile.region;

import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.global.view.region.N2oLineRegion;
import net.n2oapp.framework.api.metadata.meta.region.LineRegion;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import org.springframework.stereotype.Component;

import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.property;
import static net.n2oapp.framework.api.metadata.local.util.CompileUtil.castDefault;

/**
 * Компиляция региона с горизонтальным делителем
 */
@Component
public class LineRegionCompiler extends BaseRegionCompiler<LineRegion, N2oLineRegion> {

    @Override
    protected String getSrcProperty() {
        return "n2o.api.region.line.src";
    }

    @Override
    public Class<N2oLineRegion> getSourceClass() {
        return N2oLineRegion.class;
    }

    @Override
    public LineRegion compile(N2oLineRegion source, PageContext context, CompileProcessor p) {
        LineRegion region = new LineRegion();
        build(region, source, p);
        region.setContent(initContent(source.getContent(), context, p, source));
        region.setLabel(source.getLabel());
        region.setCollapsible(castDefault(source.getCollapsible(),
                () -> p.resolve(property("n2o.api.region.line.collapsible"), Boolean.class)));
        region.setHasSeparator(castDefault(source.getHasSeparator(),
                () -> p.resolve(property("n2o.api.region.line.has_separator"), Boolean.class)));
        region.setExpand(castDefault(source.getExpand(),
                () -> p.resolve(property("n2o.api.region.line.expand"), Boolean.class)));
        return region;
    }

    @Override
    protected String createId(CompileProcessor p) {
        return createId("line", p);
    }
}
