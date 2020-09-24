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
public class UnableToRemoveKAHFromOrganisationException extends RuntimeException {

    /**
     * Creates a new instance of
     * <code>UnableToRemoveKAHFromOrganisationException</code> without detail
     * message.
     */
    public UnableToRemoveKAHFromOrganisationException() {
    }

    /**
     * Constructs an instance of
     * <code>UnableToRemoveKAHFromOrganisationException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UnableToRemoveKAHFromOrganisationException(String msg) {
        super(msg);
    }
}
