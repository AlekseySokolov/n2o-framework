package net.n2oapp.framework.autotest.impl.component.cell;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import net.n2oapp.framework.autotest.api.component.cell.FileUploadCell;

import java.io.File;
import java.time.Duration;

/**
 * Загрузка файла в ячейке для автотестирования
 */
public class N2oFileUploadCell extends N2oCell implements FileUploadCell {

    @Override
    public void shouldBeEmpty(Duration... duration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public File uploadFile(File... file) {
        return element().$("input").uploadFile(file);
    }

    @Override
    public File uploadFromClasspath(String... fileName) {
        return element().$("input").uploadFromClasspath(fileName);
    }

    @Override
    public void deleteFile(int index) {
        files().get(index)
                .$(".n2o-file-uploader-remove")
                .hover().shouldBe(Condition.visible).click();
    }

    @Override
    public void shouldHaveSize(int size) {
        files().shouldHave(CollectionCondition.size(size));
    }

    @Override
    public void uploadFileShouldHaveName(int index, String fileName, Duration... duration) {
        should(
                Condition.text(fileName),
                files().get(index)
                .$(".n2o-file-uploader-file-name"),
                duration
        );
    }

    @Override
    public void uploadFileShouldHaveSize(int index, String fileSize, Duration... duration) {
        should(
                Condition.text(fileSize),
                files().get(index)
                        .$(".n2o-file-uploader-item-size"),
                duration
        );
    }

    @Override
    public void uploadFileShouldHaveLink(int index, String href) {
        files().get(index)
                .$(".n2o-file-uploader-link")
                .shouldHave(Condition.attribute("href", href));
    }

    protected ElementsCollection files() {
        return element().parent().$$(".n2o-file-uploader-files-list .n2o-file-uploader-files-item");
    }
}
