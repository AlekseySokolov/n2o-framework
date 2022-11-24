package net.n2oapp.framework.api.metadata.compile;

import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.SourceMetadata;
import net.n2oapp.framework.api.metadata.aware.IdAware;
import net.n2oapp.framework.api.metadata.validation.exception.N2oMetadataValidationException;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Процессор исходных метаданных
 */
public interface SourceProcessor {

    /**
     * Провалидировать вложенную метаданную
     *
     * @param metadata Исходная метаданная
     * @param scope    Объекты, влияющие на внутренние валидации. Должны быть разных классов.
     * @param <T>      Тип метаданной
     */
    <T extends Source> void validate(T metadata, Object... scope);

    /**
     * Заменить свойства исходной метаданной значениями перекрывающей метаданной, если они не пусты
     *
     * @param source   Исходная метаданная
     * @param override Перекрывающая метаданная
     * @param <S>      Тип значения
     * @return Исходная метаданная с перекрытыми свойствами
     */
    <S extends Source> S merge(S source, S override);

    /**
     * Получить исходный объект по идентификатору
     *
     * @param id          Идентификатор
     * @param sourceClass Класс исходного объекта
     * @param <S>         Тип исходного объекта
     * @return Исходный объект
     */
    <S extends SourceMetadata> S getSource(String id, Class<S> sourceClass);

    /**
     * Получить исходную метаданную по идентификатору или бросить исключение, если метаданная невалидна
     *
     * @param id            Идентификатор
     * @param metadataClass Класс метаданной
     * @param <T>           Тип метаданной
     * @return Метаданная или null
     */
    <T extends SourceMetadata> T getOrThrow(String id, Class<T> metadataClass);

    /**
     * Получить метаданную, оказывающую влияние на валидацию
     *
     * @param scopeClass Класс метаданной
     * @param <D>        Тип скоупа
     * @return Метаданная, оказывающая влияние на валидацию, или null
     */
    <D> D getScope(Class<D> scopeClass);

    /**
     * Заменить плейсхолдер на значение и конвертировать в класс
     *
     * @param placeholder Плейсхолдер
     * @param <T>         Тип значения
     * @return Значение
     */
    <T> T resolve(String placeholder, Class<T> clazz);

    /**
     * Заменить плейсхолдер на значение конвертировать по домену
     *
     * @param placeholder значение для конвертации
     * @param domain      Домен значения
     * @return значение
     */
    Object resolve(String placeholder, String domain);

    /**
     * Заменить плейсхолдер на значение и конвертировать с автоподбором типа
     *
     * @param placeholder значение для конвертации
     * @return значение
     */
    Object resolve(String placeholder);

    /**
     * Получить локализованное сообщение по коду и аргументам
     *
     * @param messageCode Код сообщения
     * @param arguments   Аргументы сообщения
     * @return Локализованное сообщение
     */
    String getMessage(String messageCode, Object... arguments);

    /**
     * Проверить, что объект не null
     *
     * @param something    Объект
     * @param errorMessage Сообщение о том, какой объект не должен быть null
     */
    default void checkNotNull(Object something, String errorMessage) {
        if (something == null)
            throw new N2oMetadataValidationException(getMessage(errorMessage));
    }

    /**
     * Проверить метаданную на существование
     *
     * @param id            Идентификатор метаданной
     * @param metadataClass Класс метаданной
     * @param errorMessage  Сообщение о том, какой метаданной не существует
     * @param <T>           Тип метаданной
     */
    <T extends SourceMetadata> void checkForExists(String id, Class<T> metadataClass, String errorMessage);

    /**
     * Проверить идентификатор метаданной по соглашениям об именовании
     *
     * @param metadata     Метаданная
     * @param errorMessage Сообщение о том, какой идентификатор не соответствует соглашениям об именовании
     */
    void checkId(IdAware metadata, String errorMessage);

    /**
     * Получить поток значений из массива
     *
     * @param values Массив значений
     * @param <T>    Тип значений
     * @return Поток значений или пустой поток, если массив null
     */
    default <T> Stream<T> safeStreamOf(T[] values) {
        return values == null ? Stream.empty() : Stream.of(values);
    }

    /**
     * Получить поток значений из коллекции
     *
     * @param values Коллекция значений
     * @param <T>    Тип значений
     * @return Поток значений или пустой поток, если коллекция null
     */
    default <T> Stream<T> safeStreamOf(Collection<T> values) {
        return values == null ? Stream.empty() : values.stream();
    }

    /**
     * Проверка, что у метаданной задан идентификатор
     *
     * @param metadata     Валидируемая метаданная
     * @param errorMessage Сообщение об ошибке
     */
    default void checkIdExistence(IdAware metadata, String errorMessage) {
        checkNotNull(metadata.getId(), errorMessage);
    }

    /**
     * Проверить уникальность идентификаторов в массиве
     *
     * @param list         Массив значений
     * @param errorMessage Сообщение в случае не уникальности
     * @param <T>          Тип значений
     */
    default <T extends IdAware> void checkIdsUnique(T[] list, String errorMessage) {
        if (list != null) {
            checkIdsUnique(Arrays.asList(list), errorMessage);
        }
    }

    /**
     * Проверить уникальность идентификаторов в коллекции
     *
     * @param list         Коллекция значений
     * @param errorMessage Сообщение в случае не уникальности
     * @param <T>          Тип значений
     */
    default <T extends IdAware> void checkIdsUnique(Collection<T> list, String errorMessage) {
        if (list == null)
            return;
        Set<String> uniqueSet = new HashSet<>();
        for (T item : list) {
            if (item.getId() == null)
                continue;
            if (!uniqueSet.add(item.getId())) {
                throw new N2oMetadataValidationException(getMessage(errorMessage, item.getId()));
            }
        }
    }
}
