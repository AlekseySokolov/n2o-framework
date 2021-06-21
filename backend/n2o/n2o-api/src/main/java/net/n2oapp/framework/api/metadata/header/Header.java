package net.n2oapp.framework.api.metadata.header;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.Compiled;
import net.n2oapp.framework.api.metadata.application.Logo;

import java.util.Map;

/**
 * Клиентская модель заголовока приложения
 */
@Getter
@Setter
public class Header implements Compiled {
    @JsonProperty
    private String src;
    @JsonProperty
    private String className;
    @JsonProperty
    private Map<String, String> style;
    @JsonProperty
    private Logo logo;
    @JsonProperty
    private SimpleMenu menu;
    @JsonProperty
    private SimpleMenu extraMenu;
    @JsonProperty
    private SearchBar search;
    @JsonProperty
    private SidebarSwitcher sidebarSwitcher;

    @Getter
    @Setter
    public static class SidebarSwitcher implements Compiled {
        @JsonProperty("defaultIcon")
        private String icon;
        @JsonProperty
        private String toggledIcon;
    }
}
