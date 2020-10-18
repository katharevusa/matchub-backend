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
public class DeleteTaskException extends Exception {

    /**
     * Creates a new instance of <code>DeleteTaskException</code> without detail
     * message.
     */
    public DeleteTaskException() {
    }

    /**
     * Constructs an instance of <code>DeleteTaskException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteTaskException(String msg) {
        super(msg);
    }
}
