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
public class ProjectNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>ProjectNotFoundException</code> without
     * detail message.
     */
    public ProjectNotFoundException() {
    }

    /**
     * Constructs an instance of <code>ProjectNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ProjectNotFoundException(String msg) {
        super(msg);
    }
}
