package net.n2oapp.framework.config.metadata.validation.standard.query;

import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.aware.SourceClassAware;
import net.n2oapp.framework.api.metadata.global.dao.N2oQuery;
import net.n2oapp.framework.api.metadata.global.dao.object.N2oObject;
import net.n2oapp.framework.api.metadata.validate.SourceValidator;
import net.n2oapp.framework.api.metadata.compile.SourceProcessor;
import net.n2oapp.framework.api.metadata.validation.exception.N2oMetadataValidationException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Валидатор выборки
 */
@Component
public class QueryValidator implements SourceValidator<N2oQuery>, SourceClassAware {

    @Override
    public void validate(N2oQuery n2oQuery, SourceProcessor p) {
        if (n2oQuery.getObjectId() != null)
            checkForExistsObject(n2oQuery.getId(), n2oQuery.getObjectId(), p);
        if (n2oQuery.getFields() != null) {
            checkForUniqueFields(n2oQuery.getFields(), n2oQuery.getId(), p);
            checkForUniqueFilterFields(n2oQuery.getFields(), n2oQuery.getId());
            checkForExistsFiltersInSelections(n2oQuery);
        }
        checkInvocations(n2oQuery, p);
    }

    /**
     * Проверка существования Объекта
     *
     * @param queryId  Идентификатор выборки
     * @param objectId Идентификатор объекта
     * @param p        Процессор исходных метаданных
     */
    private void checkForExistsObject(String queryId, String objectId, SourceProcessor p) {
        p.checkForExists(objectId, N2oObject.class,
                String.format("Выборка '%s' ссылается на несуществующий объект %s", queryId, objectId));
    }

    /**
     * Проверка, что поля в выборке не повторяются
     *
     * @param fields  Поля выборки
     * @param queryId Идентификатор выборки
     * @param p       Процессор исходных метаданных
     */
    private void checkForUniqueFields(N2oQuery.Field[] fields, String queryId, SourceProcessor p) {
        p.checkIdsUnique(fields, "Поле {0} встречается более чем один раз в выборке " + queryId);
    }

    /**
     * Проверка, что фильтруемые поля в выборке не повторяются
     *
     * @param fields  Поля выборки
     * @param queryId Идентификатор выборки
     */
    private void checkForUniqueFilterFields(N2oQuery.Field[] fields, String queryId) {
        Set<String> filterFields = new HashSet<>();
        for (N2oQuery.Field field : fields) {
            if (!field.isSearchUnavailable()) {
                for (N2oQuery.Filter filter : field.getFilterList()) {
                    if (filter.getFilterId() != null && !filterFields.add(filter.getFilterId())) {
                        throw new N2oMetadataValidationException(String.format("Фильтр %s в выборке %s повторяется", filter.getFilterId(), queryId));
                    }
                }
            }
        }
    }

    /**
     * Проверка, что фильтры указанные в <list>, <unique>, <count> существуют
     *
     * @param query Выборка
     */
    private void checkForExistsFiltersInSelections(N2oQuery query) {
        Set<String> filterFields = Arrays.stream(query.getFields())
                .filter(f -> f.getFilterList() != null)
                .flatMap(f -> Arrays.stream(f.getFilterList()))
                .map(N2oQuery.Filter::getFilterId)
                .collect(Collectors.toSet());
        checkFiltersExistInSelectionType(query.getLists(), filterFields, "list");
        checkFiltersExistInSelectionType(query.getUniques(), filterFields, "unique");
        checkFiltersExistInSelectionType(query.getCounts(), filterFields, "count");
    }

    private void checkFiltersExistInSelectionType(N2oQuery.Selection[] selections, Set<String> filterFields, String selectionType) {
        if (selections != null) {
            for (N2oQuery.Selection s : selections) {
                if (s.getFilters() != null) {
                    for (String filter : s.getFilters().split("\\s*,\\s*")) {
                        if (!filterFields.contains(filter))
                            throw new N2oMetadataValidationException(String.format("<%s> ссылается на несуществующий фильтр %s", selectionType, filter));
                    }
                }
            }
        }
    }

    /**
     * Проверка вызовов провайдеров данных
     *
     * @param query Выборка
     * @param p     Процессор исходных метаданных
     */
    private void checkInvocations(N2oQuery query, SourceProcessor p) {
        if (query.getLists() != null)
            validateInvocations(query.getLists(), p);
        if (query.getCounts() != null)
            validateInvocations(query.getCounts(), p);
        if (query.getUniques() != null)
            validateInvocations(query.getUniques(), p);
    }

    /**
     * Валидирование вызовов провайдеров данных
     *
     * @param selections Массив selection элементов в выборке
     * @param p          Процессор исходных метаданных
     */
    private void validateInvocations(N2oQuery.Selection[] selections, SourceProcessor p) {
        Arrays.stream(selections)
                .map(N2oQuery.Selection::getInvocation)
                .filter(Objects::nonNull)
                .forEach(p::validate);
    }


    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oQuery.class;
    }
}
