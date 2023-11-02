package net.n2oapp.framework.config.metadata.compile.validation;

import net.n2oapp.framework.api.data.validation.MandatoryValidation;
import net.n2oapp.framework.api.exception.SeverityType;
import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.global.dao.validation.N2oMandatoryValidation;
import org.springframework.stereotype.Component;

import static net.n2oapp.framework.api.metadata.local.util.CompileUtil.castDefault;

/**
 * Компиляция валидации обязательности заполнения поля
 */
@Component
public class MandatoryValidationCompiler extends BaseValidationCompiler<MandatoryValidation, N2oMandatoryValidation> {

    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oMandatoryValidation.class;
    }

    @Override
    public MandatoryValidation compile(N2oMandatoryValidation source, CompileContext<?, ?> context, CompileProcessor p) {
        MandatoryValidation validation = new MandatoryValidation();
        compileValidation(validation, source, p);
        validation.setSeverity(castDefault(source.getSeverity(), SeverityType.danger));
        return validation;
    }
}
