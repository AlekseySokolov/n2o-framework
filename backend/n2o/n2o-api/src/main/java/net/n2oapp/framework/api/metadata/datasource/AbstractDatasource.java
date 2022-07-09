package net.n2oapp.framework.api.metadata.datasource;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.Compiled;
import net.n2oapp.framework.api.metadata.aware.IdAware;
import net.n2oapp.framework.api.metadata.global.view.page.DefaultValuesMode;
import net.n2oapp.framework.api.metadata.meta.Dependency;

import java.util.List;

/**
 * Абстрактная реализация клиентской модели источника данных
 */
@Getter
@Setter
public abstract class AbstractDatasource implements Compiled, IdAware {

    @JsonProperty
    private String id;
    private DefaultValuesMode defaultValuesMode;
    @JsonProperty
    private List<Dependency> dependencies;
}
