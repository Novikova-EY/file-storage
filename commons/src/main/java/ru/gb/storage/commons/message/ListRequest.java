package ru.gb.storage.commons.message;

public class ListRequest extends Message {
    private String path;

    public ListRequest(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
