package com.simplesmartapps.chatsystem.data.remote.exception;

public class ConnectionException extends Throwable {
    public ConnectionException(String message, Throwable error) {
        super(message, error);
    }
}
