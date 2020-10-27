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
public class LikePostException extends Exception {

    /**
     * Creates a new instance of <code>LikePostException</code> without detail
     * message.
     */
    public LikePostException() {
    }

    /**
     * Constructs an instance of <code>LikePostException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public LikePostException(String msg) {
        super(msg);
    }
}
