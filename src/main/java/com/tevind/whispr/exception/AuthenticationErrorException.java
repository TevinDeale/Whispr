package com.tevind.whispr.exception;

import org.springframework.security.core.Authentication;

public class AuthenticationErrorException extends RuntimeException{
    public AuthenticationErrorException(String message) {
        super(message);
    }
}
