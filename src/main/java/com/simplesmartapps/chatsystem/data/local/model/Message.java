package com.simplesmartapps.chatsystem.data.local.model;

import java.util.Objects;

public final class Message {
    private final long id;
    private final String remoteUserId;
    private final MessageType type;
    private final long timestamp;
    private final boolean isIncoming;
    private final String content;

    public Message(long id, String remoteUserId, MessageType type, long timestamp, boolean isIncoming, String content) {
        this.id = id;
        this.remoteUserId = remoteUserId;
        this.type = type;
        this.timestamp = timestamp;
        this.isIncoming = isIncoming;
        this.content = content;
    }

    public long id() {
        return id;
    }

    public String remoteUserId() {
        return remoteUserId;
    }

    public MessageType type() {
        return type;
    }

    public long timestamp() {
        return timestamp;
    }

    public boolean isIncoming() {
        return isIncoming;
    }

    public String content() {
        return content;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Message) obj;
        return this.id == that.id &&
                Objects.equals(this.remoteUserId, that.remoteUserId) &&
                Objects.equals(this.type, that.type) &&
                this.timestamp == that.timestamp &&
                Objects.equals(this.isIncoming, that.isIncoming) &&
                Objects.equals(this.content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, remoteUserId, type, timestamp, isIncoming, content);
    }

    @Override
    public String toString() {
        return "Message[" +
                "id=" + id + ", " +
                "remoteUserId=" + remoteUserId + ", " +
                "type=" + type + ", " +
                "timestamp=" + timestamp + ", " +
                "isIncoming=" + isIncoming + ", " +
                "content=" + content + ']';
    }

}
