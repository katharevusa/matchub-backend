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
public class UpdatePostException extends RuntimeException {

    /**
     * Creates a new instance of <code>UpdatePostException</code> without detail
     * message.
     */
    public UpdatePostException() {
    }

    /**
     * Constructs an instance of <code>UpdatePostException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdatePostException(String msg) {
        super(msg);
    }
}
