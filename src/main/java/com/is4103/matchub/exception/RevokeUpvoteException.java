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
public class RevokeUpvoteException extends Exception {

    /**
     * Creates a new instance of <code>RevokeUpvoteException</code> without
     * detail message.
     */
    public RevokeUpvoteException() {
    }

    /**
     * Constructs an instance of <code>RevokeUpvoteException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public RevokeUpvoteException(String msg) {
        super(msg);
    }
}
