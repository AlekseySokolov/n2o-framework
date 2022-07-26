package net.n2oapp.framework.config.io.toolbar.v2;

import net.n2oapp.framework.api.metadata.global.view.action.LabelType;
import net.n2oapp.framework.api.metadata.global.view.widget.table.ShapeType;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.N2oAbstractButton;
import net.n2oapp.framework.api.metadata.io.IOProcessor;
import net.n2oapp.framework.api.metadata.meta.badge.Position;
import net.n2oapp.framework.config.io.action.v2.ActionIOv2;
import net.n2oapp.framework.config.io.control.ComponentIO;
import org.jdom2.Element;
import org.jdom2.Namespace;

/**
 * Чтение\запись содержимого Toolbar версии 2.0
 */
public abstract class AbstractButtonIOv2<T extends N2oAbstractButton> extends ComponentIO<T> {

    public static Namespace NAMESPACE = Namespace.getNamespace("http://n2oapp.net/framework/config/schema/button-2.0");

    protected Namespace actionDefaultNamespace = ActionIOv2.NAMESPACE;

    @Override
    public void io(Element e, T mi, IOProcessor p) {
        super.io(e, mi, p);
        p.attribute(e, "id", mi::getId, mi::setId);
        p.attribute(e, "label", mi::getLabel, mi::setLabel);
        p.attribute(e, "icon", mi::getIcon, mi::setIcon);
        p.attributeEnum(e, "type", mi::getType, mi::setType, LabelType.class);
        p.attribute(e, "badge", mi::getBadge, mi::setBadge);
        p.attribute(e, "badge-color", mi::getBadgeColor, mi::setBadgeColor);
        p.attributeEnum(e, "badge-position", mi::getBadgePosition, mi::setBadgePosition, Position.class);
        p.attributeEnum(e, "badge-shape", mi::getBadgeShape, mi::setBadgeShape, ShapeType.class);
        p.attribute(e, "badge-image", mi::getBadgeImage, mi::setBadgeImage);
        p.attributeEnum(e, "badge-image-position", mi::getBadgeImagePosition, mi::setBadgeImagePosition, Position.class);
        p.attributeEnum(e, "badge-image-shape", mi::getBadgeImageShape, mi::setBadgeImageShape, ShapeType.class);
        p.attribute(e, "color", mi::getColor, mi::setColor);
        p.attribute(e, "description", mi::getDescription, mi::setDescription);
        p.attribute(e, "tooltip-position", mi::getTooltipPosition, mi::setTooltipPosition);
    }

    @Override
    public String getNamespaceUri() {
        return NAMESPACE.getURI();
    }
}
