package net.n2oapp.framework.config.metadata.validation.standard.menu;

import net.n2oapp.framework.api.StringUtils;
import net.n2oapp.framework.api.metadata.compile.SourceProcessor;
import net.n2oapp.framework.api.metadata.action.N2oAnchor;
import net.n2oapp.framework.api.metadata.compile.enums.Color;
import net.n2oapp.framework.api.metadata.menu.N2oSimpleMenu;
import net.n2oapp.framework.api.metadata.validation.TypedMetadataValidator;
import net.n2oapp.framework.api.metadata.validation.exception.N2oMetadataValidationException;
import org.apache.commons.lang3.EnumUtils;

/**
 * Валидатор простого меню навигации
 */
public class SimpleMenuValidator extends TypedMetadataValidator<N2oSimpleMenu> {

    @Override
    public Class<N2oSimpleMenu> getSourceClass() {
        return N2oSimpleMenu.class;
    }

    @Override
    public void validate(N2oSimpleMenu simpleMenu, SourceProcessor p) {
        validateItems(simpleMenu.getMenuItems(), p);
    }

    private void validateDropdown(N2oSimpleMenu.DropdownMenuItem dropdownMenu, SourceProcessor p) {
        if (dropdownMenu.getName() == null)
            throw new N2oMetadataValidationException("Не задан 'name' для <dropdown-menu>");
        validateItems(dropdownMenu.getMenuItems(), p);
    }

    private void validateMenuItem(N2oSimpleMenu.MenuItem menuItem) {
        if (menuItem.getAction() instanceof N2oAnchor && menuItem.getName() == null)
            throw new N2oMetadataValidationException("Не задан 'name' для <menu-item>");

        if (menuItem.getBadgeColor() != null && !StringUtils.isLink(menuItem.getBadgeColor()) &&
                !EnumUtils.isValidEnum(Color.class, menuItem.getBadgeColor())) {
            throw new N2oMetadataValidationException(
                    String.format("<menu-item> использует недопустимое значение атрибута badge-color=\"%s\"",
                            menuItem.getBadgeColor()));
        }
    }

    private void validateItems(N2oSimpleMenu.AbstractMenuItem[] items, SourceProcessor p) {
        p.safeStreamOf(items)
                .forEach(i -> {
                    if (i instanceof N2oSimpleMenu.MenuItem)
                        validateMenuItem((N2oSimpleMenu.MenuItem) i);
                    else if (i instanceof N2oSimpleMenu.DropdownMenuItem)
                        validateDropdown((N2oSimpleMenu.DropdownMenuItem) i, p);
                });
    }
}