package net.n2oapp.framework.config.metadata.pack;

import net.n2oapp.framework.api.pack.MetadataPack;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.io.action.AnchorElementIOV1;
import net.n2oapp.framework.config.io.action.OpenPageElementIOV1;
import net.n2oapp.framework.config.metadata.compile.application.*;
import net.n2oapp.framework.config.metadata.compile.datasource.StompDatasourceCompiler;
import net.n2oapp.framework.config.metadata.compile.events.StompEventCompiler;
import net.n2oapp.framework.config.metadata.compile.header.SearchBarCompiler;
import net.n2oapp.framework.config.metadata.compile.menu.SimpleMenuCompiler;
import net.n2oapp.framework.config.metadata.compile.menu.SimpleMenuIOv2;
import net.n2oapp.framework.config.metadata.compile.menu.SimpleMenuIOv3;

/**
 * Набор для сборки приложения
 */
public class N2oApplicationPack implements MetadataPack<N2oApplicationBuilder> {
    @Override
    public void build(N2oApplicationBuilder b) {
        b.ios(new ApplicationIO(), new ApplicationIOv2(), new ApplicationIOv3(), new SimpleMenuIOv2(), new SimpleMenuIOv3(),
                new SidebarIOv3(), new OpenPageElementIOV1(), new AnchorElementIOV1());
        b.compilers(new ApplicationCompiler(), new SimpleMenuCompiler(), new SearchBarCompiler(),
                new StompDatasourceCompiler(), new StompEventCompiler(), new SidebarCompiler());
        b.binders(new ApplicationBinder());
    }
}
