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
public class UnableToSaveResourceException extends RuntimeException {

    /**
     * Creates a new instance of <code>UnableToSaveResourceException</code>
     * without detail message.
     */
    public UnableToSaveResourceException() {
    }

    /**
     * Constructs an instance of <code>UnableToSaveResourceException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public UnableToSaveResourceException(String msg) {
        super(msg);
    }
}
