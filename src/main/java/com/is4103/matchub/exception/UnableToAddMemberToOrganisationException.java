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
public class UnableToAddMemberToOrganisationException extends RuntimeException {

    /**
     * Creates a new instance of
     * <code>UnableToAddMemberToOrganisationException</code> without detail
     * message.
     */
    public UnableToAddMemberToOrganisationException() {
    }

    /**
     * Constructs an instance of
     * <code>UnableToAddMemberToOrganisationException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public UnableToAddMemberToOrganisationException(String msg) {
        super(msg);
    }
}
