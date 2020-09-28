/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.exception;

/**
 *
 * @author ngjin
 */
public class UnableToCreateProjectBadgeException extends RuntimeException {

    /**
     * Creates a new instance of
     * <code>UnableToCreateProjectBadgeException</code> without detail message.
     */
    public UnableToCreateProjectBadgeException() {
    }

    /**
     * Constructs an instance of
     * <code>UnableToCreateProjectBadgeException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public UnableToCreateProjectBadgeException(String msg) {
        super(msg);
    }
}
