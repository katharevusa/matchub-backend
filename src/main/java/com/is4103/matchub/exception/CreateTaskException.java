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
public class CreateTaskException extends Exception {

    /**
     * Creates a new instance of <code>CreateTaskException</code> without detail
     * message.
     */
    public CreateTaskException() {
    }

    /**
     * Constructs an instance of <code>CreateTaskException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateTaskException(String msg) {
        super(msg);
    }
}
