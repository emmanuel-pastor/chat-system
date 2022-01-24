package com.simplesmartapps.chatsystem.presentation.messaging;

import com.simplesmartapps.chatsystem.data.local.model.Message;
import com.simplesmartapps.chatsystem.data.local.model.User;

import java.util.Objects;

public final class UserWithLatestMessage {
    private final User user;
    private final Message latestMessage;
    private final boolean isUnread;

    UserWithLatestMessage(User user, Message latestMessage, boolean isUnread) {
        this.user = user;
        this.latestMessage = latestMessage;
        this.isUnread = isUnread;
    }

    public User user() {
        return user;
    }

    public Message latestMessage() {
        return latestMessage;
    }

    public boolean isUnread() {
        return isUnread;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (UserWithLatestMessage) obj;
        return Objects.equals(this.user, that.user) &&
                Objects.equals(this.latestMessage, that.latestMessage) &&
                this.isUnread == that.isUnread;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, latestMessage, isUnread);
    }

    @Override
    public String toString() {
        return "UserWithLatestMessage[" +
                "user=" + user + ", " +
                "latestMessage=" + latestMessage + ", " +
                "isUnread=" + isUnread + ']';
    }
}
