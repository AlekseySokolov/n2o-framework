package net.n2oapp.framework.config.metadata.cache;

import net.n2oapp.framework.api.metadata.meta.page.SimplePage;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.compile.pipeline.operation.LocalizedCompileCacheOperation;
import net.n2oapp.framework.config.compile.pipeline.operation.LocalizedSourceCacheOperation;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import net.n2oapp.framework.config.metadata.pack.N2oActionsPack;
import net.n2oapp.framework.config.metadata.pack.N2oPagesPack;
import net.n2oapp.framework.config.metadata.pack.N2oRegionsPack;
import net.n2oapp.framework.config.metadata.pack.N2oWidgetsPack;
import net.n2oapp.framework.config.selective.CompileInfo;
import net.n2oapp.framework.config.test.SourceCompileTestBase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Тестирование компиляции кэш операции с локализацией
 */
public class LocalizedCompileTest extends SourceCompileTestBase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oPagesPack(), new N2oRegionsPack(), new N2oWidgetsPack(), new N2oActionsPack())
                .operations(new LocalizedCompileCacheOperation<>(), new LocalizedSourceCacheOperation<>())
                .sources(new CompileInfo("net/n2oapp/framework/config/metadata/compile/cache/testLocalizedCompile.page.xml"));
    }

    @Test
    public void test() {
        LocaleContextHolder.setLocale(new Locale("ru"));
        SimplePage page = (SimplePage) compile("net/n2oapp/framework/config/metadata/compile/cache/testLocalizedCompile.page.xml")
                .get(new PageContext("testLocalizedCompile"));
        assertThat(page.getWidget().getName(), is("Иван"));
    }
}
