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
public class DeleteResourceRequestException extends Exception {

    /**
     * Creates a new instance of <code>DeleteResourceRequestException</code>
     * without detail message.
     */
    public DeleteResourceRequestException() {
    }

    /**
     * Constructs an instance of <code>DeleteResourceRequestException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteResourceRequestException(String msg) {
        super(msg);
    }
}
