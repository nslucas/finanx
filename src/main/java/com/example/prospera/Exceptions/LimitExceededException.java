package com.example.prospera.Exceptions;

public class LimitExceededException extends RuntimeException {
    public LimitExceededException(String msg) {
        super(msg);
    }
}
