package net.n2oapp.framework.config.io.control.v2;

import net.n2oapp.framework.config.io.control.v2.list.PillsIOv2;
import net.n2oapp.framework.config.io.widget.v4.FormElementIOV4;
import net.n2oapp.framework.config.selective.ION2oMetadataTester;
import org.junit.Test;

public class N2oPillsIOv2Test {

    @Test
    public void test() {
        ION2oMetadataTester tester = new ION2oMetadataTester();
        tester.ios(new PillsIOv2(), new FormElementIOV4());
        assert tester.check("net/n2oapp/framework/config/io/control/v2/testPills.widget.xml");
    }
}
