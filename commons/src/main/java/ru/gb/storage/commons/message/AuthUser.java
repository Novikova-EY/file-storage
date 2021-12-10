package ru.gb.storage.commons.message;

public class AuthUser extends Message {
    private User user;

    public User getUser() {
        return user;
    }

    public AuthUser(User user) {
        this.user = user;
    }
}
