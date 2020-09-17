/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.exception;

/**
 *
 * @author longluqian
 */
public class TerminateResourceException extends Exception {

    /**
     * Creates a new instance of <code>TerminateResourceException</code> without
     * detail message.
     */
    public TerminateResourceException() {
    }

    /**
     * Constructs an instance of <code>TerminateResourceException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public TerminateResourceException(String msg) {
        super(msg);
    }
}
