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
public class DeleteProjectException extends Exception {

    /**
     * Creates a new instance of <code>DeleteProjectException</code> without
     * detail message.
     */
    public DeleteProjectException() {
    }

    /**
     * Constructs an instance of <code>DeleteProjectException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteProjectException(String msg) {
        super(msg);
    }
}
