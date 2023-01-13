package net.n2oapp.framework.api.metadata.global.view.region;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.RegionItem;
import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.SourceComponent;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oWidget;

import java.util.List;
import java.util.Map;

/**
 * Исходная модель региона с отслеживанием прокрутки
 */
@Getter
@Setter
public class N2oScrollspyRegion extends N2oRegion implements RoutableRegion {
    private String title;
    private String active;
    private String placement;
    private Boolean headlines;
    private String activeParam;
    private Boolean routable;
    private AbstractMenuItem[] menu;

    @Override
    public String getAlias() {
        return "scrollspy";
    }

    @Getter
    @Setter
    public static class MenuItem extends AbstractMenuItem {
        private SourceComponent[] content;

        @Override
        public void collectWidgets(List<N2oWidget> result, Map<String, Integer> ids, String prefix) {
            if (content != null) {
                if (!ids.containsKey(prefix))
                    ids.put(prefix, 1);
                for (SourceComponent component : content) {
                    if (component instanceof RegionItem)
                        ((RegionItem) component).collectWidgets(result, ids, prefix);
                }
            }
        }
    }

    @Getter
    @Setter
    public static class SubMenuItem extends AbstractMenuItem {
        private AbstractMenuItem[] subMenu;

        @Override
        public void collectWidgets(List<N2oWidget> result, Map<String, Integer> ids, String prefix) {
            if (subMenu != null) {
                if (!ids.containsKey(prefix))
                    ids.put(prefix, 1);
                for (AbstractMenuItem mi : subMenu) {
                    mi.collectWidgets(result, ids, prefix);
                }
            }
        }
    }

    @Getter
    @Setter
    public static abstract class AbstractMenuItem implements Source, RegionItem {
        private String id;
        private String title;
    }

    @Override
    public void collectWidgets(List<N2oWidget> result, Map<String, Integer> ids, String prefix) {
        if (menu != null) {
            for (AbstractMenuItem mi : menu) {
                mi.collectWidgets(result, ids, getAlias());
            }
        }
    }
}
