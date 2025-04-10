package com.users.app.advice.exception;

public class PhoneAlreadyRegisteredException extends RuntimeException {
    public PhoneAlreadyRegisteredException(String message) {
        super(message);
    }
}
