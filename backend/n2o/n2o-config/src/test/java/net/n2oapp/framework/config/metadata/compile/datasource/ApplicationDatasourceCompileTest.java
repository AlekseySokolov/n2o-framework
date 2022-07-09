package net.n2oapp.framework.config.metadata.compile.datasource;

import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.datasource.ApplicationDatasource;
import net.n2oapp.framework.api.metadata.meta.CopyDependency;
import net.n2oapp.framework.api.metadata.meta.Dependency;
import net.n2oapp.framework.api.metadata.meta.DependencyType;
import net.n2oapp.framework.api.metadata.meta.page.StandardPage;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.io.control.v3.plain.InputTextIOv3;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import net.n2oapp.framework.config.metadata.pack.N2oAllDataPack;
import net.n2oapp.framework.config.metadata.pack.N2oAllPagesPack;
import net.n2oapp.framework.config.test.SourceCompileTestBase;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Тестирование компиляции источника данных, ссылающегося на источник из application.xml
 */
public class ApplicationDatasourceCompileTest extends SourceCompileTestBase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oAllPagesPack(), new N2oAllDataPack()).ios(new InputTextIOv3());
    }

    @Test
    public void simple() {
        StandardPage page = (StandardPage)
                compile("net/n2oapp/framework/config/metadata/compile/datasource/testApplicationDatasource.page.xml")
                        .get(new PageContext("testApplicationDatasource"));

        ApplicationDatasource datasource = (ApplicationDatasource) page.getDatasources().get("testApplicationDatasource_appDatasource");

        assertThat(datasource.getDependencies().size(), is(2));

        Dependency dependency = datasource.getDependencies().get(0);
        assertThat(dependency.getOn(), is("models.resolve['testApplicationDatasource_ds']"));
        assertThat(dependency.getType(), is(DependencyType.fetch));

        dependency = datasource.getDependencies().get(1);
        assertThat(dependency.getOn(), is("models.resolve['testApplicationDatasource_ds']"));
        assertThat(dependency.getType(), is(DependencyType.copy));
        assertThat(((CopyDependency) dependency).getField(), is("target"));
        assertThat(((CopyDependency) dependency).getModel(), is(ReduxModel.resolve));

        assertThat(datasource.getProvider().getType(), is("application"));
        assertThat(datasource.getProvider().getDatasource(), is("appDatasource"));
        assertThat(datasource.getId(), is("testApplicationDatasource_appDatasource"));
    }
}
