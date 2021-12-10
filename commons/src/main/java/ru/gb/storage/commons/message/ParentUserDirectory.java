package ru.gb.storage.commons.message;

public class ParentUserDirectory extends Message {
    private String dir;

    public String getDir() {
        return dir;
    }

    public ParentUserDirectory(String path) {
        this.dir = path;
    }
}
