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
public class UploadOrganisationVerificationDocException extends RuntimeException {

    /**
     * Creates a new instance of
     * <code>UploadOrganisationVerificationDocException</code> without detail
     * message.
     */
    public UploadOrganisationVerificationDocException() {
    }

    /**
     * Constructs an instance of
     * <code>UploadOrganisationVerificationDocException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UploadOrganisationVerificationDocException(String msg) {
        super(msg);
    }
}
