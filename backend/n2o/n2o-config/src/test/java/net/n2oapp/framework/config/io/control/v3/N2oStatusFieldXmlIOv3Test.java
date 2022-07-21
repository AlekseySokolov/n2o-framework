package net.n2oapp.framework.config.io.control.v3;

import net.n2oapp.framework.config.io.widget.v5.FormElementIOV5;
import net.n2oapp.framework.config.selective.ION2oMetadataTester;
import org.junit.Test;

/**
 * Тестирование чтения/записи компонента отображения статуса
 */
public class N2oStatusFieldXmlIOv3Test {

    @Test
    public void test() {
        ION2oMetadataTester tester = new ION2oMetadataTester();
        tester.ios(new StatusFieldIOv3(), new FormElementIOV5());
        assert tester.check("net/n2oapp/framework/config/io/control/v3/testStatusFieldV3.widget.xml");
    }
}