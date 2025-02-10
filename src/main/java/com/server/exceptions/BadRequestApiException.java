package com.server.exceptions;

public class BadRequestApiException extends RuntimeException {

    public BadRequestApiException(String msg) {
        super(msg);
    }
}
