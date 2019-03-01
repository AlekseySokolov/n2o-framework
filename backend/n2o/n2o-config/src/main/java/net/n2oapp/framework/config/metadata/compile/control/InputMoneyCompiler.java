package net.n2oapp.framework.config.metadata.compile.control;

import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.control.plain.FractionFormatting;
import net.n2oapp.framework.api.metadata.control.plain.N2oInputMoney;
import net.n2oapp.framework.api.metadata.meta.control.InputMoney;
import net.n2oapp.framework.api.metadata.meta.control.StandardField;
import org.springframework.stereotype.Component;

@Component
public class InputMoneyCompiler extends StandardFieldCompiler<InputMoney, N2oInputMoney> {
    @Override
    protected String getControlSrcProperty() {
        return "n2o.api.control.input.money.src";
    }

    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oInputMoney.class;
    }

    @Override
    public StandardField<InputMoney> compile(N2oInputMoney source, CompileContext<?, ?> context, CompileProcessor p) {
        InputMoney inputMoney = new InputMoney();
        inputMoney.setPrefix(source.getPrefix());
        inputMoney.setSuffix(source.getSuffix());
        inputMoney.setThousandsSeparatorSymbol(source.getThousandsSeparator());
        inputMoney.setDecimalSymbol(source.getDecimalSeparator());
        inputMoney.setIntegerLimit(source.getIntegerLimit());
        compileDecimalMode(inputMoney, source);
        return compileStandardField(inputMoney, source, context, p);
    }

    private void compileDecimalMode(InputMoney inputMoney, N2oInputMoney source) {
        if (source.getFractionFormatting() == null || FractionFormatting.off.equals(source.getFractionFormatting())) return;
        switch (source.getFractionFormatting()) {
            case manual: {
                inputMoney.setAllowDecimal(true);
                inputMoney.setRequireDecimal(false);
                break;
            }
            case auto: {
                inputMoney.setAllowDecimal(true);
                inputMoney.setRequireDecimal(true);
                break;
            }
        }
    }
}
