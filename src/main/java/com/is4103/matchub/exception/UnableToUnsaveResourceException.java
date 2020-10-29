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
public class UnableToUnsaveResourceException extends RuntimeException {

    /**
     * Creates a new instance of <code>UnableToUnsaveResourceException</code>
     * without detail message.
     */
    public UnableToUnsaveResourceException() {
    }

    /**
     * Constructs an instance of <code>UnableToUnsaveResourceException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public UnableToUnsaveResourceException(String msg) {
        super(msg);
    }
}
