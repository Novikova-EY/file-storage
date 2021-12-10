package ru.gb.storage.commons.message;

import ru.gb.storage.commons.message.Message;

public class FileRequest extends Message {
    private String fileName;

    public String getFilename() {
        return fileName;
    }

    public FileRequest(String filename) {
        this.fileName = filename;
    }
}
