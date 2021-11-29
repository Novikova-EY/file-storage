package ru.gb.storage.commons.message.file;


import ru.gb.storage.commons.message.Message;

public class FileMessage extends Message {

    private String name;
    private String path;
    private byte[] content;
    private long position;
    public boolean fileTransfer = false;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public boolean isFileTransfer() {
        return fileTransfer;
    }

    public void setFileTransfer(boolean fileTransfer) {
        this.fileTransfer = fileTransfer;
    }

}
