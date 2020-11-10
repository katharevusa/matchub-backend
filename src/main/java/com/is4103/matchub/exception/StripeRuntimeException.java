package com.is4103.matchub.exception;

public class StripeRuntimeException extends RuntimeException {

    public StripeRuntimeException() {
    }

    public StripeRuntimeException(String message) {
        super(message);
    }
}
