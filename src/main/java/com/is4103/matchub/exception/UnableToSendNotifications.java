package com.is4103.matchub.exception;

/**
 *
 * @author tjle2
 */
public class UnableToSendNotifications extends RuntimeException {

    public UnableToSendNotifications() {
    }

    public UnableToSendNotifications(String message) {
        super(message);
    }
}
