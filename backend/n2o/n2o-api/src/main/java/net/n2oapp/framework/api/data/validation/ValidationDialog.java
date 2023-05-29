package net.n2oapp.framework.api.data.validation;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.criteria.dataset.DataSetUtil;
import net.n2oapp.framework.api.StringUtils;
import net.n2oapp.framework.api.data.DomainProcessor;
import net.n2oapp.framework.api.data.InvocationProcessor;
import net.n2oapp.framework.api.metadata.global.dao.object.AbstractParameter;
import net.n2oapp.framework.api.metadata.global.view.page.N2oDialog;
import net.n2oapp.framework.api.metadata.local.CompiledObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Клиентская модель валидации с диалогом выбора
 */
@Getter
@Setter
public class ValidationDialog extends InvocationValidation {
    private N2oDialog dialog;

    @Override
    public void validate(DataSet dataSet, InvocationProcessor serviceProvider, ValidationFailureCallback callback,
                         DomainProcessor domainProcessor) {
        dataSet = domainProcessor.doDomainConversation(dataSet, getInParametersList());
        DataSet result;
        if (getInvocation() != null)
            result = serviceProvider.invoke(getInvocation(), dataSet, getInParametersList(), getOutParametersList());
        else {
            Map<String, String> outMapping = new LinkedHashMap<>();
            if (getOutParametersList() != null)
                for (AbstractParameter parameter : getOutParametersList())
                    outMapping.put(parameter.getId(), parameter.getMapping());
            result = DataSetUtil.extract(dataSet, outMapping);
        }

        if (result.get(CompiledObject.VALIDATION_RESULT_PARAM) == null || !(boolean) result.get(CompiledObject.VALIDATION_RESULT_PARAM))
            callback.onFail(StringUtils.resolveLinks(getMessage(), result));
    }

    @Override
    public String getType() {
        return "dialog";
    }
}
