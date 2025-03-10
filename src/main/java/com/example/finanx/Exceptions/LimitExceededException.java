package com.example.finanx.Exceptions;

public class LimitExceededException extends RuntimeException {
    public LimitExceededException(String msg) {
        super(msg);
    }
}
