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
public class JoinProjectException extends Exception {

    /**
     * Creates a new instance of <code>JoinProjectException</code> without
     * detail message.
     */
    public JoinProjectException() {
    }

    /**
     * Constructs an instance of <code>JoinProjectException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public JoinProjectException(String msg) {
        super(msg);
    }
}
