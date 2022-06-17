package net.n2oapp.framework.api.metadata.datasource;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.data.validation.Validation;
import net.n2oapp.framework.api.metadata.global.view.page.N2oBrowserStorageDatasource;
import net.n2oapp.framework.api.metadata.meta.DependencyCondition;

import java.util.List;
import java.util.Map;

/**
 * Клиентская модель источника данных для localStorage в браузере
 */
@Getter
@Setter
public class BrowserStorageDatasource extends AbstractDatasource {

    @JsonProperty
    private Provider provider;
    @JsonProperty
    private Map<String, List<Validation>> validations;
    @JsonProperty
    private Integer size;
    @JsonProperty
    private Submit submit;
    @JsonProperty
    private List<DependencyCondition> dependencies;


    @Getter
    @Setter
    public static class Provider {
        private String type = "browser";
        private String key;
        private N2oBrowserStorageDatasource.BrowserStorageType storage;
    }

    @Getter
    @Setter
    public static class Submit {
        private String type = "browser";
        private Boolean auto;
        private String key;
        private N2oBrowserStorageDatasource.BrowserStorageType storage;
    }
}
