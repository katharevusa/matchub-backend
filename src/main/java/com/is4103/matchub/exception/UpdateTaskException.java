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
public class UpdateTaskException extends Exception {

    /**
     * Creates a new instance of <code>UpdateTaskException</code> without detail
     * message.
     */
    public UpdateTaskException() {
    }

    /**
     * Constructs an instance of <code>UpdateTaskException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateTaskException(String msg) {
        super(msg);
    }
}