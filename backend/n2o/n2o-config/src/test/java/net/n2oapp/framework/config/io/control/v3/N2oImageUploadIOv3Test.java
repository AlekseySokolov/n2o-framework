package net.n2oapp.framework.config.io.control.v3;

import net.n2oapp.framework.config.io.widget.v5.FormElementIOV5;
import net.n2oapp.framework.config.selective.ION2oMetadataTester;
import org.junit.jupiter.api.Test;

/**
 * Тестирование чтения/записи компонента загрузки изображений
 */
public class N2oImageUploadIOv3Test {

    @Test
    void test() {
        ION2oMetadataTester tester = new ION2oMetadataTester();
        tester.ios(new ImageUploadIOv3(), new FormElementIOV5());
        assert tester.check("net/n2oapp/framework/config/io/control/v3/testImageUploadV3.widget.xml");
    }
}
