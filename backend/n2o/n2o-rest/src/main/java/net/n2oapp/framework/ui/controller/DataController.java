package net.n2oapp.framework.ui.controller;

import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.MetadataEnvironment;
import net.n2oapp.framework.api.metadata.meta.saga.*;
import net.n2oapp.framework.api.register.route.MetadataRouter;
import net.n2oapp.framework.api.rest.ControllerFactory;
import net.n2oapp.framework.api.rest.GetDataResponse;
import net.n2oapp.framework.api.rest.SetDataResponse;
import net.n2oapp.framework.api.ui.ActionRequestInfo;
import net.n2oapp.framework.api.ui.ActionResponseInfo;
import net.n2oapp.framework.api.ui.QueryRequestInfo;
import net.n2oapp.framework.api.ui.QueryResponseInfo;
import net.n2oapp.framework.api.user.UserContext;
import net.n2oapp.framework.config.register.route.RouteUtil;

import java.util.Map;
import java.util.Set;

import static net.n2oapp.framework.engine.util.MappingProcessor.resolveCondition;

/**
 * Контроллер данных
 */
public class DataController extends AbstractController {

    private ControllerFactory controllerFactory;

    public DataController(ControllerFactory controllerFactory,
                          MetadataEnvironment environment) {
        super(environment);
        this.controllerFactory = controllerFactory;
    }

    public DataController(ControllerFactory controllerFactory,
                          MetadataEnvironment environment,
                          MetadataRouter router) {
        super(environment, router);
        this.controllerFactory = controllerFactory;
    }

    public GetDataResponse getData(String path, Map<String, String[]> parameters, UserContext user) {
        QueryRequestInfo requestInfo = createQueryRequestInfo(path, parameters, user);
        QueryResponseInfo responseInfo = new QueryResponseInfo();
        responseInfo.setAlertMessageBuilder(getMessageBuilder());
        return controllerFactory.execute(requestInfo, responseInfo);
    }

    public SetDataResponse setData(String path, Map<String, String[]> parameters, Map<String, String[]> headers, Object body, UserContext user) {
        ActionRequestInfo requestInfo = createActionRequestInfo(path, parameters, headers, body, user);
        ActionResponseInfo responseInfo = new ActionResponseInfo();
        responseInfo.setAlertMessageBuilder(getMessageBuilder());
        SetDataResponse result = controllerFactory.execute(requestInfo, responseInfo);
        resolveMeta(requestInfo, result);
        return result;
    }

    private void resolveMeta(ActionRequestInfo requestInfo, SetDataResponse response) {
        if (requestInfo.getPollingEndCondition() != null && !resolveCondition(requestInfo.getPollingEndCondition(), response.getData())) {
            resolvePolling(requestInfo, response);
        } else {
            resolveRedirect(requestInfo, response);
            resolveRefresh(requestInfo, response);
            resolveLoading(requestInfo, response);
        }
    }

    private void resolveLoading(ActionRequestInfo requestInfo, SetDataResponse response) {
        if (requestInfo.getLoading() == null)
            return;
        LoadingSaga loading = new LoadingSaga();
        loading.setPageId(requestInfo.getLoading().getPageId());
        loading.setPosition(requestInfo.getLoading().getPosition());
        loading.setActive(requestInfo.getLoading().getActive());

        if (response.getMeta() == null)
            response.setMeta(new MetaSaga());
        response.getMeta().setLoading(loading);
    }

    private void resolvePolling(ActionRequestInfo requestInfo, SetDataResponse response) {
        PollingSaga polling = new PollingSaga();
        polling.setDelay(requestInfo.getPolling().getDelay());
        polling.setDataProvider(requestInfo.getPolling().getDataProvider());
        polling.setDatasource(requestInfo.getPolling().getDatasource());
        polling.setModel(requestInfo.getPolling().getModel());

        if (response.getMeta() == null)
            response.setMeta(new MetaSaga());
        response.getMeta().setPolling(polling);
    }

    private void resolveRedirect(ActionRequestInfo requestInfo, SetDataResponse response) {
        if (requestInfo.getRedirect() == null)
            return;
        RedirectSaga redirect = requestInfo.getRedirect();
        if (response.getData() != null) {
            DataSet data = new DataSet(response.getData());
            data.merge(requestInfo.getQueryData());
            String redirectUrl = redirect.getPath();
            Set<String> except = redirect.getPathMapping() != null ? redirect.getPathMapping().keySet() : null;
            redirectUrl = RouteUtil.resolveUrlParams(redirectUrl, requestInfo.getQueryData(), null, except);
            redirectUrl = RouteUtil.resolveUrlParams(redirectUrl, response.getData(), null, except);
            RedirectSaga resolvedRedirect = new RedirectSaga();
            resolvedRedirect.setTarget(redirect.getTarget());
            resolvedRedirect.setPathMapping(redirect.getPathMapping());
            resolvedRedirect.setQueryMapping(redirect.getQueryMapping());
            resolvedRedirect.setPath(redirectUrl);
            response.addRedirect(resolvedRedirect);
        }
    }

    private void resolveRefresh(ActionRequestInfo requestInfo, SetDataResponse response) {
        if (requestInfo.getRefresh() != null) {
            RefreshSaga resolvedRefresh = new RefreshSaga();
            resolvedRefresh.setDatasources(requestInfo.getRefresh().getDatasources());

            if (response.getMeta() == null) response.setMeta(new MetaSaga());
            response.getMeta().setRefresh(resolvedRefresh);
        }
    }
}
