package net.n2oapp.framework.config.io.control.v3.list;

import net.n2oapp.criteria.filters.FilterType;
import net.n2oapp.framework.api.metadata.control.N2oListField;
import net.n2oapp.framework.api.metadata.global.dao.N2oPreFilter;
import net.n2oapp.framework.api.metadata.io.IOProcessor;
import net.n2oapp.framework.api.metadata.meta.badge.Position;
import net.n2oapp.framework.api.metadata.meta.badge.Shape;
import net.n2oapp.framework.config.io.control.v3.StandardFieldIOv3;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.HashMap;
import java.util.Map;

/**
 * Чтение, запись спискового поля версии 3.0
 * @param <T>
 */
public abstract class ListFieldIOv3<T extends N2oListField> extends StandardFieldIOv3<T> {
    @Override
    public void io(Element e, T m, IOProcessor p) {
        super.io(e, m, p);
        p.attribute(e, "query-id", m::getQueryId, m::setQueryId);
        p.attribute(e, "label-field-id", m::getLabelFieldId, m::setLabelFieldId);
        p.attribute(e, "value-field-id", m::getValueFieldId, m::setValueFieldId);
        p.attribute(e, "sort-field-id", m::getSortFieldId, m::setSortFieldId);
        p.attribute(e, "badge-field-id", m::getBadgeFieldId, m::setBadgeFieldId);
        p.attribute(e, "badge-color-field-id", m::getBadgeColorFieldId, m::setBadgeColorFieldId);
        p.attribute(e, "badge-image-field-id", m::getBadgeImageFieldId, m::setBadgeImageFieldId);
        p.attribute(e, "search-filter-id", m::getSearchFilterId, m::setSearchFilterId);
        p.attribute(e, "group-field-id", m::getGroupFieldId, m::setGroupFieldId);
        p.attribute(e, "image-field-id", m::getImageFieldId, m::setImageFieldId);
        p.attribute(e, "icon-field-id", m::getIconFieldId, m::setIconFieldId);
        p.attribute(e, "format", m::getFormat, m::setFormat);
        p.attribute(e, "enabled-field-id", m::getEnabledFieldId, m::setEnabledFieldId);
        p.attributeBoolean(e, "search", m::getSearch, m::setSearch);
        p.attributeBoolean(e, "cache", m::getCache, m::setCache);
        p.attributeInteger(e, "size", m::getSize, m::setSize);
        p.attributeEnum(e, "badge-position", m::getBadgePosition, m::setBadgePosition, Position.class);
        p.attributeEnum(e, "badge-image-position", m::getBadgeImagePosition, m::setBadgeImagePosition, Position.class);
        p.attributeEnum(e, "badge-shape", m::getBadgeShape, m::setBadgeShape, Shape.class);
        p.attributeEnum(e, "badge-image-shape", m::getBadgeImageShape, m::setBadgeImageShape, Shape.class);
        p.child(e, null, "default-value", m::getDefValue, m::setDefValue, HashMap::new, this::defaultValue);
        p.children(e, "options", "option", m::getOptions, m::setOptions, HashMap::new, this::option);
        p.childrenByEnum(e, "filters", m::getPreFilters, m::setPreFilters, N2oPreFilter::getType,
                N2oPreFilter::setType, N2oPreFilter::new, FilterType.class, this::prefilter);
        p.attribute(e, "status-field-id", m::getStatusFieldId, m::setStatusFieldId);
    }

    private void option(Element e, Map<String, String> map, IOProcessor p) {
        p.otherAttributes(e, Namespace.NO_NAMESPACE, map);
    }

    private void defaultValue(Element e, Map<String, String> map, IOProcessor p) {
        p.otherAttributes(e, Namespace.NO_NAMESPACE, map);
    }
}
