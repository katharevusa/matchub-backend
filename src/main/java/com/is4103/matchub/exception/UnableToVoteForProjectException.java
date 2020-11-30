package com.is4103.matchub.exception;

/**
 *
 * @author tjle2
 */
public class UnableToVoteForProjectException extends RuntimeException {

    public UnableToVoteForProjectException() {
    }

    public UnableToVoteForProjectException(String message) {
        super(message);
    }
}
