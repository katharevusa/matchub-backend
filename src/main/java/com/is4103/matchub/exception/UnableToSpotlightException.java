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
public class UnableToSpotlightException extends RuntimeException {

    /**
     * Creates a new instance of <code>UnableToSpotlightException</code> without
     * detail message.
     */
    public UnableToSpotlightException() {
    }

    /**
     * Constructs an instance of <code>UnableToSpotlightException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public UnableToSpotlightException(String msg) {
        super(msg);
    }
}
