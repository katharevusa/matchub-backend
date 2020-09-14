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
public class TerminateProjectException extends Exception {

    /**
     * Creates a new instance of <code>TerminateProjectException</code> without
     * detail message.
     */
    public TerminateProjectException() {
    }

    /**
     * Constructs an instance of <code>TerminateProjectException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public TerminateProjectException(String msg) {
        super(msg);
    }
}
