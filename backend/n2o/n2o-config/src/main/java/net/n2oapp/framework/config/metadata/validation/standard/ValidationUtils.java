package net.n2oapp.framework.config.metadata.validation.standard;

import net.n2oapp.framework.api.metadata.aware.IdAware;
import net.n2oapp.framework.api.metadata.aware.NamespaceUriAware;
import net.n2oapp.framework.api.metadata.compile.SourceProcessor;
import net.n2oapp.framework.api.metadata.validation.exception.N2oMetadataValidationException;
import net.n2oapp.framework.config.metadata.compile.datasource.DatasourceIdsScope;

/**
 * Утилиты проверки метаданных
 */
public final class ValidationUtils {

    private ValidationUtils() {
    }

    /**
     * Проверить идентификаторы метаданных по соглашениям об именовании
     *
     * @param items Метаданные
     * @param p     Процессор исходных метаданных
     */
    public static void checkIds(NamespaceUriAware[] items, SourceProcessor p) {
        if (items != null) {
            for (NamespaceUriAware item : items) {
                checkId(item, p);
            }
        }
    }

    /**
     * Проверить идентификатор метаданной по соглашениям об именовании
     *
     * @param item Метаданная
     * @param p    Процессор исходных метаданных
     */
    public static void checkId(NamespaceUriAware item, SourceProcessor p) {
        if (item instanceof IdAware) {
            p.checkId((IdAware) item, "Идентификатор поля {0} является запрещенным именем");
        }
    }

    /**
     * Проверка существования источника данных в скоупе
     *
     * @param dsId               Идентификатор проверямого источника данных
     * @param datasourceIdsScope Скоуп источников данных
     * @param msg                Сообщение об ошибке
     */
    public static void checkForExistsDatasource(String dsId, DatasourceIdsScope datasourceIdsScope, String msg) {
        if (datasourceIdsScope != null) {
            if (!datasourceIdsScope.contains(dsId))
                throw new N2oMetadataValidationException(msg);
        }
    }

    /**
     * Получение идентификатора метаданной для сообщения исключений
     *
     * @param metadataId Идентификатор метаданной
     * @return Идентификатор метаданной в случае его существования, иначе пуста строка
     */
    public static String getIdOrEmptyString(String metadataId) {
        return metadataId != null ? metadataId : "";
    }
}
