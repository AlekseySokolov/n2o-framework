package net.n2oapp.framework.sandbox.autotest.examples;

import net.n2oapp.framework.autotest.api.collection.Regions;
import net.n2oapp.framework.autotest.api.component.page.LeftRightPage;
import net.n2oapp.framework.autotest.api.component.region.PanelRegion;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.sandbox.autotest.SandboxAutotestApplication;
import net.n2oapp.framework.sandbox.autotest.SandboxAutotestBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"n2o.sandbox.project-id=examples_panels"},
        classes = SandboxAutotestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PanelsAT extends SandboxAutotestBase {

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
    }

    @Test
    public void testPanels() {
        LeftRightPage page = open(LeftRightPage.class);
        page.shouldExists();
        page.header().shouldHaveBrandName("N2O");
        page.breadcrumb().crumb(0).shouldHaveLabel("Страница с двумя панелями");

        Regions left = page.left();
        left.shouldHaveSize(1);
        left.region(0, PanelRegion.class).shouldHaveTitle("Панель слева");

        Regions right = page.right();
        right.shouldHaveSize(1);
        right.region(0, PanelRegion.class).shouldHaveTitle("Панель справа");
    }

}

