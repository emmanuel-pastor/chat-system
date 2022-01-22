package com.simplesmartapps.chatsystem.data.local.model;

public enum MessageType {
    TEXT_MESSAGE("TEXT_MESSAGE"),
    FILE("FILE");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
