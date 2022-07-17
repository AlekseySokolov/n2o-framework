package net.n2oapp.framework.config.metadata.compile.page;

import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.global.view.page.N2oSearchablePage;
import net.n2oapp.framework.api.metadata.meta.page.SearchablePage;
import net.n2oapp.framework.api.metadata.meta.region.Region;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import net.n2oapp.framework.config.metadata.compile.widget.SearchBarScope;
import net.n2oapp.framework.config.util.CompileUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.property;

/**
 * Компиляция страницы с регионами и поисковой строкой
 */
@Component
public class SearchablePageCompiler extends BasePageCompiler<N2oSearchablePage, SearchablePage> {

    @Override
    public SearchablePage compile(N2oSearchablePage source, PageContext context, CompileProcessor p) {
        SearchablePage page = new SearchablePage();
        initDefaults(source, p);
        SearchBarScope searchBarScope = new SearchBarScope(source.getSearchBar().getDatasourceId(), source.getSearchBar().getSearchFilterId());
        searchBarScope.setParam(source.getSearchBar().getSearchParam());
        page = compilePage(source, page, context, p, searchBarScope);
        page.setSearchBar(compileSearchBar(source, p));
        return page;
    }

    private void initDefaults(N2oSearchablePage source, CompileProcessor p) {
        source.setSearchBar(initSearchBar(source.getSearchBar(), p));
    }

    private N2oSearchablePage.N2oSearchBar initSearchBar(N2oSearchablePage.N2oSearchBar source, CompileProcessor p) {
        N2oSearchablePage.N2oSearchBar result = source;
        if (result == null)
            result = new N2oSearchablePage.N2oSearchBar();
        result.setSearchParam(p.cast(result.getSearchParam(), result.getDatasourceId() + "_" + result.getSearchFilterId()));
        return result;
    }

    @Override
    protected Map<String, List<Region>>  initRegions(N2oSearchablePage source, SearchablePage page, CompileProcessor p,
                                                     PageContext context, Object... scopes) {
        Map<String, List<Region>> regions = new HashMap<>();
        initRegions(source.getItems(), regions, "single", context, p, scopes);
        return regions;
    }

    protected SearchablePage.SearchBar compileSearchBar(N2oSearchablePage source, CompileProcessor p) {
        SearchablePage.SearchBar searchBar = new SearchablePage.SearchBar();
        searchBar.setClassName(source.getSearchBar().getClassName());
        searchBar.setTrigger(SearchablePage.SearchBar.TriggerType.valueOf(p.resolve(property("n2o.api.page.searchable.trigger"), String.class)));
        searchBar.setPlaceholder(source.getSearchBar().getPlaceholder());
        if (SearchablePage.SearchBar.TriggerType.BUTTON.equals(searchBar.getTrigger())) {
            searchBar.setButton(new SearchablePage.SearchBar.Button());
        } else if (SearchablePage.SearchBar.TriggerType.CHANGE.equals(searchBar.getTrigger())) {
            searchBar.setThrottleDelay(p.resolve(property("n2o.api.page.searchable.throttle-delay"), Integer.class));
        }
        searchBar.setFieldId(source.getSearchBar().getSearchFilterId());
        searchBar.setDatasource(CompileUtil.getClientDatasourceId(source.getSearchBar().getDatasourceId(), p));
        return searchBar;
    }

    @Override
    protected String getSrcProperty() {
        return "n2o.api.page.searchable.src";
    }

    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oSearchablePage.class;
    }
}