package ru.gb.storage.commons.message.request.auth;

import ru.gb.storage.commons.message.Message;

public class AuthOkMessage extends Message {
    private boolean isAuthOk = false;

    public AuthOkMessage(boolean isAuthOk) {
        this.isAuthOk = isAuthOk;
    }

    public boolean isAuthOk() {
        return isAuthOk;
    }
}
