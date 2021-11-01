package net.n2oapp.framework.config.io.cell.v3;

import net.n2oapp.framework.config.io.action.v2.CloseActionElementIOV2;
import net.n2oapp.framework.config.io.widget.table.TableElementIOV5;
import net.n2oapp.framework.config.metadata.pack.N2oCellsV3IOPack;
import net.n2oapp.framework.config.selective.ION2oMetadataTester;
import org.junit.Test;

/**
 * Тестирование чтения/записи ячейки с изображением
 */
public class ImageCellXmlIOTest {
    @Test
    public void testImageCell() {
        ION2oMetadataTester tester = new ION2oMetadataTester();
        tester.ios(new TableElementIOV5(), new CloseActionElementIOV2())
                .addPack(new N2oCellsV3IOPack());

        assert tester.check("net/n2oapp/framework/config/io/cell/v3/testImageCellIOv3.widget.xml");
    }
}
