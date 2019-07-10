package net.n2oapp.framework.api.metadata.global.dao;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.criteria.filters.FilterType;
import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.compile.building.Placeholders;

import java.util.Objects;

import static net.n2oapp.criteria.filters.FilterType.Arity.n_ary;


/**
 * Исходная модель предустановленного фильтра
 */
@Getter
@Setter
public class N2oPreFilter implements Source {
    /**
     * Поле фильтрации
     */
    private String fieldId;
    /**
     * Значение фильтра value
     */
    private String valueAttr;
    /**
     * Значение фильтра values
     */
    private String valuesAttr;
    /**
     * Тип фильтрации
     */
    private FilterType type;
    /**
     * Виджет, который нужно фильтровать
     */
    private String targetWidgetId;
    /**
     * Наименование параметра в адресе маршрута
     */
    private String param;
    /**
     * Идентификатор страницы, на которую ссылается фильтр
     */
    private String refPageId;
    /**
     * Идентификатор виджета на странице, на который ссылается фильтр
     */
    private String refWidgetId;
    /**
     * Модель виджета, на который ссылается фильтр
     */
    private ReduxModel refModel;

    /**
     * Список значений фильтра
     */
    private String[] valueList;

    /**
     * Обязательность фильтра
     */
    private Boolean required;

    private ResetMode resetMode;
    private Boolean onChange;

    public N2oPreFilter() {
    }

    public N2oPreFilter(String fieldId, String value, FilterType type) {
        this.fieldId = fieldId;
        this.type = type;
        this.valueAttr = value;
    }

    public N2oPreFilter(String fieldId, String[] values, FilterType type) {
        this.fieldId = fieldId;
        this.type = type;
        this.valueList = values;
    }

    public String getRef() {
        if (getValue() != null && getValue().startsWith("{") && getValue().endsWith("}")) {
            return getValue().substring(1, getValue().length() - 1);
        } else
            return null;
    }

    public void setRef(String ref) {
        this.valueAttr = Placeholders.ref(ref);
    }

    public boolean isRef() {
        return getRef() != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof N2oPreFilter)) return false;
        N2oPreFilter that = (N2oPreFilter) o;
        return Objects.equals(fieldId, that.fieldId) &&
                Objects.equals(refWidgetId, that.refWidgetId) &&
                refModel == that.refModel &&
                type == that.type;
    }

    public boolean isBoolean() {
        return FilterType.isNull.equals(getType()) || FilterType.isNotNull.equals(getType());
    }

    public boolean isArray() {
        return (getValues() != null && getValues().length != 0)
                || (type.arity.equals(n_ary) && valueAttr != null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldId, refWidgetId, refModel, type);
    }


    public enum ResetMode {
        on, off
    }

    public String[] getValues() {
        if (valueList != null && valueList.length > 0)
            return valueList;
        else if (type != null && type.arity.equals(n_ary) && valueAttr != null)
            return new String[]{valueAttr};
        else
            return null;
    }

    public String getValue() {
        return valueAttr == null ? valuesAttr : valueAttr;
    }

    @Override
    public String toString() {
        return "N2oPreFilter{" +
                "fieldId='" + fieldId + '\'' +
                ", type=" + type +
                '}';
    }
}
