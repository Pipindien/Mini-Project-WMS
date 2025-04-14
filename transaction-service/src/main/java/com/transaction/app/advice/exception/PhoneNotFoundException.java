package com.transaction.app.advice.exception;

public class PhoneNotFoundException extends RuntimeException {
    public PhoneNotFoundException(String message) {
        super(message);
    }
}
