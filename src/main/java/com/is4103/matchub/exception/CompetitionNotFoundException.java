package com.is4103.matchub.exception;

/**
 *
 * @author tjle2
 */
public class CompetitionNotFoundException extends RuntimeException {

    public CompetitionNotFoundException() {
    }

    public CompetitionNotFoundException(String message) {
        super(message);
    }
}
