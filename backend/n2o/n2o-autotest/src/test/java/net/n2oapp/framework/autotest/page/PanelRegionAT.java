package net.n2oapp.framework.autotest.page;

import net.n2oapp.framework.autotest.Colors;
import net.n2oapp.framework.autotest.api.component.page.StandardPage;
import net.n2oapp.framework.autotest.api.component.region.PanelRegion;
import net.n2oapp.framework.autotest.run.AutoTestBase;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.pack.N2oAllPagesPack;
import net.n2oapp.framework.config.metadata.pack.N2oHeaderPack;
import net.n2oapp.framework.config.selective.CompileInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Автотест для региона в виде панели
 */
public class PanelRegionAT extends AutoTestBase {

    @BeforeAll
    public static void beforeClass() {
        configureSelenide();
    }

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oAllPagesPack(), new N2oHeaderPack());
        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/simple/test.header.xml"));
    }

    @Test
    public void testPanelRegion() {
        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/region/panel/index.page.xml"));
        StandardPage page = open(StandardPage.class);
        page.shouldExists();

        PanelRegion panel1 = page.place("single").region(0, PanelRegion.class);
        panel1.shouldExists();
        panel1.shouldHaveTitle("Panel1");
        panel1.shouldHaveBorderColor(Colors.DANGER);
        panel1.shouldHaveIcon("fa-exclamation");
        panel1.shouldBeCollapsible();
        panel1.shouldBeCollapsed();
        panel1.expandContent();
        panel1.shouldBeExpanded();
        panel1.shouldHaveFooterTitle("Footer");
        panel1.collapseContent();
        panel1.shouldBeCollapsed();

        // not collapsible panel
        PanelRegion panel2 = page.place("single").region(1, PanelRegion.class);
        panel2.shouldExists();
        panel2.shouldHaveTitle("Panel2");
        panel2.shouldNotBeCollapsible();

        // panel without title
        PanelRegion panel3 = page.place("single").region(2, PanelRegion.class);
        panel3.shouldExists();
        panel3.shouldNotHaveTitle();
        panel3.shouldNotBeCollapsible();
    }
}
