/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.exception;

/**
 *
 * @author ngjin
 */
public class UnableToUnfollowProfileException extends RuntimeException {

    /**
     * Creates a new instance of <code>UnableToUnfollowProfileException</code>
     * without detail message.
     */
    public UnableToUnfollowProfileException() {
    }

    /**
     * Constructs an instance of <code>UnableToUnfollowProfileException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public UnableToUnfollowProfileException(String msg) {
        super(msg);
    }
}
