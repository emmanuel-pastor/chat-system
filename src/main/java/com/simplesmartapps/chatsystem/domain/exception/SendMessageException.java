package com.simplesmartapps.chatsystem.domain.exception;

public class SendMessageException extends Exception {
    public SendMessageException(String message, Throwable error) {
        super(message, error);
    }
}
