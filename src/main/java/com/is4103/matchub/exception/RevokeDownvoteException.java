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
public class RevokeDownvoteException extends Exception {

    /**
     * Creates a new instance of <code>RevokeDownvoteException</code> without
     * detail message.
     */
    public RevokeDownvoteException() {
    }

    /**
     * Constructs an instance of <code>RevokeDownvoteException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public RevokeDownvoteException(String msg) {
        super(msg);
    }
}
