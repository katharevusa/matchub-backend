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
public class UnableToRemoveFollowerException extends RuntimeException {

    /**
     * Creates a new instance of <code>UnableToRemoveFollowerException</code>
     * without detail message.
     */
    public UnableToRemoveFollowerException() {
    }

    /**
     * Constructs an instance of <code>UnableToRemoveFollowerException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public UnableToRemoveFollowerException(String msg) {
        super(msg);
    }
}
