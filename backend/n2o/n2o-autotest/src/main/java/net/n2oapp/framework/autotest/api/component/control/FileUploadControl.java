package net.n2oapp.framework.autotest.api.component.control;

import java.io.File;
import java.time.Duration;

/**
 * Загрузка файла для автотестирования
 */
public interface FileUploadControl extends Control {

    /**
     * Загрузка файлов
     * @param file список загружаемых файлов
     * @return первый загруженный файл
     */
    File uploadFile(File... file);

    /**
     * Загрузка файлов через classpath
     * @param fileName список имен загружаемых файлов
     * @return первый загруженный файл
     */
    File uploadFromClasspath(String... fileName);

    /**
     * Удаление файла из поля ввода по номеру
     * @param index номер файла
     */
    void deleteFile(int index);

    /**
     * Проверка количества загруженных файлов
     * @param count ожидаемое количество загруженных файлов
     */
    void shouldHaveUploadFiles(int count);

    /**
     * Проверка названия загруженного файла
     * @param index номер проверяемого файла
     * @param fileName ожидаемое имя файла
     */
    void uploadFileShouldHaveName(int index, String fileName, Duration... duration);

    /**
     * Проверка размера загруженного файлов
     * @param index номер проверяемого файла
     * @param fileSize ожидаемый размер файла
     */
    void uploadFileShouldHaveSize(int index, String fileSize);

    /**
     * Проверка ссылки загруженного файлов
     * @param index номер проверяемого файла
     * @param href ожидаемая ссылка файла
     */
    void uploadFileShouldHaveLink(int index, String href);
}
