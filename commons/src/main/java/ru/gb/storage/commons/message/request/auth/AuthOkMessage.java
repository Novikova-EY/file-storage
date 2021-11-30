package ru.gb.storage.commons.message.request.auth;

import ru.gb.storage.commons.message.Message;

public class AuthOkMessage extends Message {
    private boolean isAuthOk = false;

    public boolean isAuthOk() {
        return isAuthOk;
    }

    public void setAuthOk(boolean authOk) {
        isAuthOk = authOk;
    }
}
