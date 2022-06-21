package net.n2oapp.framework.config.metadata.validation.standard.datasource;

import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.aware.SourceClassAware;
import net.n2oapp.framework.api.metadata.compile.SourceProcessor;
import net.n2oapp.framework.api.metadata.global.dao.N2oPreFilter;
import net.n2oapp.framework.api.metadata.global.dao.N2oQuery;
import net.n2oapp.framework.api.metadata.global.dao.object.N2oObject;
import net.n2oapp.framework.api.metadata.global.view.page.N2oStandardDatasource;
import net.n2oapp.framework.api.metadata.validate.SourceValidator;
import net.n2oapp.framework.api.metadata.validation.exception.N2oMetadataValidationException;
import net.n2oapp.framework.config.metadata.compile.datasource.DatasourceIdsScope;
import net.n2oapp.framework.config.metadata.compile.widget.WidgetScope;
import net.n2oapp.framework.config.metadata.validation.standard.ValidationUtils;
import org.springframework.stereotype.Component;

/**
 * Валидатор исходного источника данных
 */
@Component
public class StandardDatasourceValidator implements SourceValidator<N2oStandardDatasource>, SourceClassAware {

    private String datasourceId;

    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oStandardDatasource.class;
    }

    @Override
    public void validate(N2oStandardDatasource datasource, SourceProcessor p) {
        setDatasourceId(datasource, p);
        checkForExistsObject(datasource, p);
        N2oQuery query = checkQueryExists(datasource, p);
        DatasourceIdsScope scope = p.getScope(DatasourceIdsScope.class);
        checkDependencies(datasource, scope);
        checkSubmit(datasource, scope);
        checkPrefilters(datasource, query, scope, p);
    }

    /**
     * Проверка существования объекта источника данных
     * @param datasource Источник данных
     * @param p          Процессор исходных метаданных
     */
    private void checkForExistsObject(N2oStandardDatasource datasource, SourceProcessor p) {
        p.checkForExists(datasource.getObjectId(), N2oObject.class,
                String.format("Источник данных %s ссылается на несуществующий объект %s", datasourceId, datasource.getObjectId()));
    }

    /**
     * Проверка существования источников данных, указанных в зависимостях
     * @param datasource Источник данных, зависимости которого проверяются
     * @param scope      Скоуп источников данных
     */
    private void checkDependencies(N2oStandardDatasource datasource, DatasourceIdsScope scope) {
        if (datasource.getDependencies() != null) {
            for (N2oStandardDatasource.Dependency d : datasource.getDependencies()) {
                if (d instanceof N2oStandardDatasource.FetchDependency && ((N2oStandardDatasource.FetchDependency) d).getOn() != null) {
                    String on = ((N2oStandardDatasource.FetchDependency) d).getOn();
                    ValidationUtils.checkForExistsDatasource(on, scope,
                            String.format("Атрибут \"on\" в зависимости источника данных %s ссылается на несуществующий источник данных %s",
                                    datasourceId, on));
                }
            }
        }
    }

    /**
     * Проверка существования источников данных, содержащихся в сабмите
     * @param datasource Источник данных, сабмит которого исследуется
     * @param scope      Скоуп источников данных
     */
    private void checkSubmit(N2oStandardDatasource datasource, DatasourceIdsScope scope) {
        if (datasource.getSubmit() != null && datasource.getSubmit().getRefreshDatasources() != null) {
            for (String refreshDs : datasource.getSubmit().getRefreshDatasources()) {
                ValidationUtils.checkForExistsDatasource(refreshDs, scope,
                        String.format("Тег <submit> источника данных %s содержит несуществующий источник данных '%s' в атрибуте \"refresh-datasources\"",
                                datasourceId, refreshDs));
            }
        }
    }

    /**
     * Проверка валидации префильтров источника данных
     * @param datasource Источник данных
     * @param query      Запрос за данными
     * @param scope      Скоуп источников данных
     * @param p          Процессор исходных метаданных
     */
    private void checkPrefilters(N2oStandardDatasource datasource, N2oQuery query, DatasourceIdsScope scope, SourceProcessor p) {
        if (datasource.getFilters() != null) {
            if (query == null)
                throw new N2oMetadataValidationException(
                        String.format("Источник данных %s имеет префильтры, но не задана выборка", datasourceId));
            if (query.getFields() == null)
                throw new N2oMetadataValidationException(
                        String.format("Источник данных %s имеет префильтры, но в выборке '%s' нет fields!", datasourceId, query.getId()));

            for (N2oPreFilter preFilter : datasource.getFilters()) {
                String fieldId = ValidationUtils.getIdOrEmptyString(preFilter.getFieldId());
                String queryId = ValidationUtils.getIdOrEmptyString(query.getId());

                if (preFilter.getDatasource() != null)
                    ValidationUtils.checkForExistsDatasource(preFilter.getDatasource(), scope,
                            String.format("В префильтре по полю %s указан несуществующий источник данных '%s'",
                                    fieldId, preFilter.getDatasource()));
                N2oQuery.Field exField = null;
                for (N2oQuery.Field field : query.getFields()) {
                    if (preFilter.getFieldId().equals(field.getId())) {
                        exField = field;
                        break;
                    }
                }
                if (exField == null)
                    throw new N2oMetadataValidationException(
                            String.format("В выборке %s нет field '%s'!", queryId, preFilter.getFieldId()));

                if (exField.getFilterList() == null)
                    throw new N2oMetadataValidationException(
                            String.format("В выборке %s field '%s' не содержит фильтров!", queryId, preFilter.getFieldId()));

                N2oQuery.Filter exFilter = null;
                for (N2oQuery.Filter filter : exField.getFilterList()) {
                    if (preFilter.getType() == filter.getType()) {
                        exFilter = filter;
                        break;
                    }
                }
                if (exFilter == null)
                    throw new N2oMetadataValidationException(
                            String.format("В выборке %s field '%s' не содержит фильтр типа '%s'!",
                                    queryId,
                                    preFilter.getFieldId(),
                                    preFilter.getType()));
            }
        }
    }

    /**
     * Проверка сущестования выборки источника данных
     * @param datasource Источник данных
     * @param p          Процессор исходных метаданных
     * @return           Метаданная выборки если она существует, иначе null
     */
    private N2oQuery checkQueryExists(N2oStandardDatasource datasource, SourceProcessor p) {
        if (datasource.getQueryId() != null) {
            p.checkForExists(datasource.getQueryId(), N2oQuery.class,
                    String.format("Источник данных %s ссылается на несуществующую выборку '%s'", datasourceId, datasource.getQueryId()));
            return p.getOrThrow(datasource.getQueryId(), N2oQuery.class);
        }
        return null;
    }

    /**
     * Определение идентификатора источника данных для сообщений в исключениях
     * @param datasource Источник даныых
     * @param p          Процессор исходных метаданных
     */
    private void setDatasourceId(N2oStandardDatasource datasource, SourceProcessor p) {
        WidgetScope widgetScope = p.getScope(WidgetScope.class);
        if (widgetScope != null)
            datasourceId = ValidationUtils.getIdOrEmptyString(widgetScope.getWidgetId());
        else
            datasourceId = datasource.getId();
    }
}
