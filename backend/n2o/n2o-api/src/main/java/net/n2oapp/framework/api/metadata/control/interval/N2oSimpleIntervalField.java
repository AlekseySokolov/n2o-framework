package net.n2oapp.framework.api.metadata.control.interval;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.control.N2oStandardField;

/**
 * Абстрактная реализация контролов с интревалами
 */
@Getter
@Setter
public abstract class N2oSimpleIntervalField extends N2oStandardField {
    private String begin;
    private String end;
    private String beginParam;
    private String endParam;
}
