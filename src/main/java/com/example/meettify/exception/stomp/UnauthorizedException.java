package com.example.meettify.exception.stomp;

import org.springframework.security.access.AccessDeniedException;

public class UnauthorizedException extends AccessDeniedException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
