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
public class RepostException extends Exception {

    /**
     * Creates a new instance of <code>RepostException</code> without detail
     * message.
     */
    public RepostException() {
    }

    /**
     * Constructs an instance of <code>RepostException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public RepostException(String msg) {
        super(msg);
    }
}
