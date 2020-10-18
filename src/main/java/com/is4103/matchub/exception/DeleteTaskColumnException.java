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
public class DeleteTaskColumnException extends Exception {

    /**
     * Creates a new instance of <code>DeleteTaskColumnException</code> without
     * detail message.
     */
    public DeleteTaskColumnException() {
    }

    /**
     * Constructs an instance of <code>DeleteTaskColumnException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteTaskColumnException(String msg) {
        super(msg);
    }
}
