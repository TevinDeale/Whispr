package com.tevind.whispr.exception;

public class ThreadNotFoundException extends  RuntimeException{
    public ThreadNotFoundException(String message) {
        super(message);
    }
}
