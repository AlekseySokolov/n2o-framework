package net.n2oapp.framework.api.config;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Конфигурация клиента N2O приложения
 */
@Getter
@Setter
public class AppConfig {
    //    @JsonIgnore
    private Map<String, Object> properties = new LinkedHashMap<>();

    public Object getProperty(String property) {
        return properties.get(property);
    }

    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        return properties;
    }

    @JsonAnySetter
    public void setProperty(String key, Object value) {
        if (this.properties.containsKey(key))
            setProperty(properties, key, value);
        else
            this.properties.put(key, value);
    }

    private void setProperty(Map<String, Object> source, String key, Object value) {
        Object obj = source.get(key);
        if (value instanceof List && obj instanceof List)
            ((List) obj).addAll((List) value);
        else if (value instanceof Map && obj instanceof Map) {
            Map<String, Object> innerSource = (Map<String, Object>) obj;
            ((Map) value).keySet().stream().forEach(k -> setProperty(innerSource, (String) k, ((Map) value).get(k)));
        } else
            source.put(key, value);
    }
}
