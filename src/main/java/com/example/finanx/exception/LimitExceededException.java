package com.example.finanx.exception;

public class LimitExceededException extends RuntimeException {
    public LimitExceededException(String msg) {
        super(msg);
    }
}
