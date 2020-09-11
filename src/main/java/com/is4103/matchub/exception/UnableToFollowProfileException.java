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
public class UnableToFollowProfileException extends RuntimeException {

    /**
     * Creates a new instance of <code>UnableToFollowProfileException</code>
     * without detail message.
     */
    public UnableToFollowProfileException() {
    }

    /**
     * Constructs an instance of <code>UnableToFollowProfileException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public UnableToFollowProfileException(String msg) {
        super(msg);
    }
}
