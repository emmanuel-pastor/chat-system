package com.simplesmartapps.chatsystem.domain.exception;

public class ConnectionException extends Exception {
    public ConnectionException(String message, Throwable error) {
        super(message, error);
    }
}
