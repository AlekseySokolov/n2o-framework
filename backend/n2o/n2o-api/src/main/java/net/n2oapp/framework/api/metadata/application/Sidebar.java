package net.n2oapp.framework.api.metadata.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.Compiled;
import net.n2oapp.framework.api.metadata.header.SimpleMenu;

import java.util.Map;

/**
 * Клиентская модель боковой панели
 */
@Getter
@Setter
public class Sidebar implements Compiled {
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
    private Side side;
    @JsonProperty
    private SidebarState defaultState;
    @JsonProperty
    private SidebarState toggledState;
    @JsonProperty
    private Boolean toggleOnHover;
    @JsonProperty
    private Boolean overlay;
}
