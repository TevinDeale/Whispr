package com.tevind.whispr.exception;

public class JwtTokenErrorException extends RuntimeException {
    public JwtTokenErrorException(String message) {
        super(message);
    }
}
