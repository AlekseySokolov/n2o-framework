package net.n2oapp.framework.ui.controller.query;

import net.n2oapp.criteria.api.CollectionPage;
import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.data.QueryProcessor;
import net.n2oapp.framework.api.exception.N2oException;
import net.n2oapp.framework.api.rest.GetDataResponse;
import net.n2oapp.framework.api.ui.AlertMessageBuilder;
import net.n2oapp.framework.api.ui.QueryRequestInfo;
import net.n2oapp.framework.api.ui.QueryResponseInfo;
import net.n2oapp.framework.api.util.SubModelsProcessor;
import net.n2oapp.framework.engine.modules.stack.DataProcessingStack;

/**
 * Абстрактный контроллер получения данных по умолчанию
 */
public abstract class DefaultValuesController extends GetController {

    protected DefaultValuesController(DataProcessingStack dataProcessingStack,
                                      QueryProcessor queryProcessor,
                                      SubModelsProcessor subModelsProcessor,
                                      AlertMessageBuilder messageBuilder) {
        super(dataProcessingStack, queryProcessor, subModelsProcessor, messageBuilder);
    }

    @Override
    public GetDataResponse execute(QueryRequestInfo requestInfo, QueryResponseInfo responseInfo) {
        try {
            DataSet defaultModel = extractDefaultModel(requestInfo, responseInfo);
            return new GetDataResponse(defaultModel, requestInfo.getCriteria(), responseInfo, requestInfo.getMessagesForm());
        } catch (N2oException e) {
            GetDataResponse response = new GetDataResponse(getMessageBuilder().buildMessages(e, requestInfo), requestInfo.getMessagesForm());
            response.setStatus(e.getHttpStatus());
            return response;
        }

    }

    protected DataSet extractDefaultModel(QueryRequestInfo requestInfo, QueryResponseInfo responseInfo) {
        DataSet defaultModel = requestInfo.getData() == null ? new DataSet() : new DataSet(requestInfo.getData());

        if (requestInfo.getQuery() != null) {
            getSubModelsProcessor().executeSubModels(requestInfo.getQuery().getSubModelQueries(), defaultModel);
            CollectionPage<DataSet> queryDefaultPage = executeQuery(requestInfo, responseInfo);
            if (!queryDefaultPage.getCollection().isEmpty()) {
                DataSet queryDefaultModel = queryDefaultPage.getCollection().iterator().next();
                defaultModel.merge(queryDefaultModel, DataSet.EXTEND_IF_VALUE_NOT_NULL);
            }
        }

        return defaultModel;
    }

}
