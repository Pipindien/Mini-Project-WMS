package com.transaction.app.advice.exception;

public class TrxNumberNotFoundException extends RuntimeException {
    public TrxNumberNotFoundException(String message) {
        super(message);
    }
}
