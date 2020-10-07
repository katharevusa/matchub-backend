package com.is4103.matchub.exception;

/**
 *
 * @author tjle2
 */
public class FirebaseRuntimeException extends RuntimeException {

    public FirebaseRuntimeException() {
    }

    public FirebaseRuntimeException(String message) {
        super(message);
    }
}
