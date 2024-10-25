package com.example.meettify.exception.timeout;

public class RequestTimeoutException extends RuntimeException {
    public RequestTimeoutException(String message) {
        super(message);
    }
}
