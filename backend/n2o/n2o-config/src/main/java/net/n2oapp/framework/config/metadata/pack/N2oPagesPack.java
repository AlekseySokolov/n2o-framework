package net.n2oapp.framework.config.metadata.pack;

import net.n2oapp.framework.api.pack.MetadataPack;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.io.datasource.*;
import net.n2oapp.framework.config.metadata.compile.datasource.BrowserStorageDatasourceCompiler;
import net.n2oapp.framework.config.metadata.compile.datasource.InheritedDatasourceCompiler;
import net.n2oapp.framework.config.metadata.compile.datasource.StandardDatasourceCompiler;
import net.n2oapp.framework.config.metadata.compile.datasource.StompDatasourceCompiler;
import net.n2oapp.framework.config.metadata.merge.datasource.N2oBrowserStorageDatasourceMerger;
import net.n2oapp.framework.config.metadata.merge.datasource.N2oInheritedDatasourceMerger;
import net.n2oapp.framework.config.metadata.merge.datasource.N2oStandardDatasourceMerger;
import net.n2oapp.framework.config.metadata.merge.datasource.N2oStompDatasourceMerger;
import net.n2oapp.framework.config.metadata.compile.page.*;

/**
 * Набор для сборки стандартных страниц
 */
public class N2oPagesPack implements MetadataPack<N2oApplicationBuilder> {

    @Override
    public void build(N2oApplicationBuilder b) {
        b.packs(new N2oPagesIOv3Pack(), new N2oPagesIOv4Pack(), new N2oAllDatasourcesPack());
        b.compilers(new SimplePageCompiler(),
                new StandardPageCompiler(),
                new LeftRightPageCompiler(),
                new TopLeftRightPageCompiler(),
                new SearchablePageCompiler());
        b.binders(new SimplePageBinder(), new StandardPageBinder());
    }
}
