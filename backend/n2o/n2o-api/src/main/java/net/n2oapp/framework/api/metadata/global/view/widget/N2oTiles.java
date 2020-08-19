package net.n2oapp.framework.api.metadata.global.view.widget;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.global.view.widget.table.column.cell.N2oCell;

import java.io.Serializable;

/**
 * Исходная модель виджета плитки
 */
@Getter
@Setter
public class N2oTiles extends N2oWidget {

    private Integer colsSm;
    private Integer colsMd;
    private Integer colsLg;
    private Block[] content;

    @Getter
    @Setter
    public static class Block implements Serializable {

        private String id;
        private String className;
        private String style;
        private String src;
        private N2oCell component;

    }

}