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
public class UpdateResourceException extends Exception {

    /**
     * Creates a new instance of <code>UpdateResourceException</code> without
     * detail message.
     */
    public UpdateResourceException() {
    }

    /**
     * Constructs an instance of <code>UpdateResourceException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateResourceException(String msg) {
        super(msg);
    }
}
