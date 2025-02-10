package com.server.exceptions;

public class NotFoundExceptionHandler extends RuntimeException {

    public NotFoundExceptionHandler(String msg) {
        super(msg);
    }
}
