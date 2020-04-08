package net.n2oapp.framework.api.metadata.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.Compiled;
import net.n2oapp.framework.api.metadata.meta.toolbar.Toolbar;

/**
 * Диалог подтверждения действия
 */
@Getter
@Setter
public class Dialog implements Compiled {
    /**
     * Заголовок диалога
     */
    @JsonProperty
    private String title;

    /**
     * Описание диалога
     */
    @JsonProperty
    private String description;

    /**
     * Тулбар
     */
    @JsonProperty
    private Toolbar toolbar;

}
