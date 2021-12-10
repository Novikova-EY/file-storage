package ru.gb.storage.commons.message;

import java.util.List;


public class ServerList extends Message{
    private  List<FilesListInfo> serverList;


    public ServerList(List<FilesListInfo> serverList) {
        this.serverList = serverList;
    }

    public List<FilesListInfo> getServerList() {
        return serverList;
    }

}
