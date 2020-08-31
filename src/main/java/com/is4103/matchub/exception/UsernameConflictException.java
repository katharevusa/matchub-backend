package com.is4103.matchub.exception;

public class UsernameConflictException extends RuntimeException {

    public UsernameConflictException(String username) {
        super("username already exists: " + username);
    }
}
