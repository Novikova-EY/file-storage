package ru.gb.storage.commons.message;

public class UserDirectory extends Message {
    private String dir;

    public String getDir() {
        return dir;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public UserDirectory(String path) {
        this.dir = path;
    }
}
