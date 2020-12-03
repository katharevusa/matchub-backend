package com.is4103.matchub.exception;

/**
 *
 * @author tjle2
 */
public class ClaimRequestNotFoundException extends RuntimeException {

    public ClaimRequestNotFoundException() {
    }

    public ClaimRequestNotFoundException(String message) {
        super(message);
    }
}
