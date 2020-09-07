package com.is4103.matchub.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super("could not find user '" + userId + "'.");
    }

    public UserNotFoundException(String email) {
        super("could not find user '" + email + "'.");
    }

    public UserNotFoundException(UUID uuid) {
        super("could not find user '" + uuid + "'.");
    }
}
