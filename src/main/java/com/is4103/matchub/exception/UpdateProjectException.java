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
public class UpdateProjectException extends Exception {

    /**
     * Creates a new instance of <code>UpdateProjectException</code> without
     * detail message.
     */
    public UpdateProjectException() {
    }

    /**
     * Constructs an instance of <code>UpdateProjectException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateProjectException(String msg) {
        super(msg);
    }
}
