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
public class ResourceNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>ResourceNotEFoundException</code> without
     * detail message.
     */
    public ResourceNotFoundException() {
    }

    /**
     * Constructs an instance of <code>ResourceNotEFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ResourceNotFoundException(String msg) {
        super(msg);
    }
}
