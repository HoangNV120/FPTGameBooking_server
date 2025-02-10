package com.server.exceptions;

public class RestApiException extends RuntimeException {

    public RestApiException(String msg) {
        super(msg);
    }
}
