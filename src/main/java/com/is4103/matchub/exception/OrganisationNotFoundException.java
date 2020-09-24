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
public class OrganisationNotFoundException extends RuntimeException {

    /**
     * Creates a new instance of <code>OrganisationNotFoundException</code>
     * without detail message.
     */
    public OrganisationNotFoundException() {
    }

    /**
     * Constructs an instance of <code>OrganisationNotFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public OrganisationNotFoundException(String msg) {
        super(msg);
    }
}
