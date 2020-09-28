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
public class UnableToUpdateBadgeException extends RuntimeException {

    /**
     * Creates a new instance of <code>UnableToUpdateBadgeException</code>
     * without detail message.
     */
    public UnableToUpdateBadgeException() {
    }

    /**
     * Constructs an instance of <code>UnableToUpdateBadgeException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public UnableToUpdateBadgeException(String msg) {
        super(msg);
    }
}
