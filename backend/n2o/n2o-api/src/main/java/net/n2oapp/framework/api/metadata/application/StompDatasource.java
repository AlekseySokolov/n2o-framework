package net.n2oapp.framework.api.metadata.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.datasource.AbstractDatasource;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Клиентская модель STOMP-источника данных
 */
@Getter
@Setter
public class StompDatasource extends AbstractDatasource {

    @JsonProperty
    private Provider provider;
    @JsonProperty
    private List<Map<String, Object>> values;

    @Getter
    @Setter
    public static class Provider implements Serializable {
        @JsonProperty
        private String type;
        @JsonProperty
        private String destination;
    }
}
