package net.n2oapp.framework.config.io.widget;

import net.n2oapp.framework.config.io.widget.tile.BlockComponentIO;
import net.n2oapp.framework.config.metadata.pack.N2oCellsIOPack;
import net.n2oapp.framework.config.selective.ION2oMetadataTester;
import org.junit.Test;


/**
 * Тестирование чтения и записи плиток версии 4
 */
public class TilesWidgetXmlIOV4Test {
    @Test
    public void testTilesXmlIOV4() {
        ION2oMetadataTester tester = new ION2oMetadataTester();

        tester.ios(new TilesWidgetIOV4(), new BlockComponentIO())
                .addPack(new N2oCellsIOPack());

        assert tester.check("net/n2oapp/framework/config/io/widget/TilesWidgetIOV4.widget.xml");
    }

}