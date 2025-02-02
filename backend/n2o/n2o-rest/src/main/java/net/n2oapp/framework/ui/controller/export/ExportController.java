package net.n2oapp.framework.ui.controller.export;

import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.MetadataEnvironment;
import net.n2oapp.framework.api.StringUtils;
import net.n2oapp.framework.api.metadata.global.dao.query.AbstractField;
import net.n2oapp.framework.api.metadata.global.dao.query.field.QueryReferenceField;
import net.n2oapp.framework.api.metadata.global.dao.query.field.QuerySimpleField;
import net.n2oapp.framework.api.rest.ExportResponse;
import net.n2oapp.framework.api.rest.GetDataResponse;
import net.n2oapp.framework.api.ui.QueryRequestInfo;
import net.n2oapp.framework.api.user.UserContext;
import net.n2oapp.framework.ui.controller.AbstractController;
import net.n2oapp.framework.ui.controller.DataController;
import net.n2oapp.framework.ui.controller.export.format.FileGeneratorFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ExportController extends AbstractController {

    private static final String FILES_DIRECTORY_NAME = System.getProperty("java.io.tmpdir");
    private static final String FILE_NAME = "export_data";
    private static final String CONTENT_TYPE = "text/";
    private static final String CONTENT_DISPOSITION_FORMAT = "attachment;filename=%s";
    private final DataController dataController;
    private final FileGeneratorFactory fileGeneratorFactory;

    public ExportController(MetadataEnvironment environment, DataController dataController, FileGeneratorFactory fileGeneratorFactory) {
        super(environment);
        this.dataController = dataController;
        this.fileGeneratorFactory = fileGeneratorFactory;
    }

    public ExportResponse export(List<DataSet> data, String format, String charset, Map<String, String> headers) {
        ExportResponse response = new ExportResponse();
        String lowerFormat = format.toLowerCase();
        byte[] fileBytes = fileGeneratorFactory.getGenerator(lowerFormat)
                .createFile(FILE_NAME, FILES_DIRECTORY_NAME, charset, data, resolveHeaders(data, headers));

        if (fileBytes == null)
            response.setStatus(500);

        response.setFile(fileBytes);
        response.setContentType(CONTENT_TYPE + lowerFormat);
        response.setContentDisposition(String.format(CONTENT_DISPOSITION_FORMAT, getFileName(lowerFormat)));
        response.setCharacterEncoding(charset);
        response.setContentLength(fileBytes == null ? 0 : fileBytes.length);

        return response;
    }

    public GetDataResponse getData(String path, Map<String, String[]> params, UserContext user) {
        GetDataResponse data = dataController.getData(path, params, user);
        leaveShowedFields(data, params.get("show"));

        return data;
    }

    public Map<String, String> getHeaders(String path, Map<String, String[]> params) {
        QueryRequestInfo queryRequestInfo = this.createQueryRequestInfo(path, params, null);
        return getFieldsNames(queryRequestInfo.getQuery().getDisplayFields());
    }

    private List<String> resolveHeaders(List<DataSet> data, Map<String, String> headers) {
        ArrayList<String> resolvedHeaders = new ArrayList<>();
        if (!data.isEmpty())
            for (String key : data.get(0).flatKeySet())
                resolvedHeaders.add(headers.get(key));

        return resolvedHeaders;
    }

    private Map<String, String> getFieldsNames(List<AbstractField> fields) {
        Map<String, String> names = new HashMap<>();
        for (AbstractField field : fields) {
            if (field instanceof QueryReferenceField) {
                names.putAll(getFieldsNames(List.of(((QueryReferenceField) field).getFields())));
                continue;
            }
            QuerySimpleField simpleField = (QuerySimpleField) field;
            names.put(simpleField.getId(), simpleField.getName());
        }

        return names;
    }

    private String getFileName(String fileFormat) {
        return FILE_NAME + "_" + System.currentTimeMillis() + "." + fileFormat;
    }

    /**
     * Функция оставляет только поля, указанные в параметре 'show'
     *
     * @param dataResponse - данные для экспорта
     * @param showedFields - имена отображаемых полей
     */
    private void leaveShowedFields(GetDataResponse dataResponse, String[] showedFields) {
        if (dataResponse.getList().isEmpty() || StringUtils.isEmpty(showedFields))
            return;

        Set<String> showed = Set.of(showedFields);
        List<String> ignore = dataResponse.getList()
                .get(0)
                .flatKeySet()
                .stream()
                .filter(f -> !showed.contains(f))
                .collect(Collectors.toList());
        dataResponse.getList().forEach(data -> ignore.forEach(data::remove));
    }
}
