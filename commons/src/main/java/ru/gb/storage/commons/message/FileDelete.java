package ru.gb.storage.commons.message;

import ru.gb.storage.commons.message.Message;

public class FileDelete extends Message {
    private String fileName;

    public FileDelete(String filename) {
        this.fileName = filename;
    }

    public String getFileName() {
        return fileName;
    }
}
