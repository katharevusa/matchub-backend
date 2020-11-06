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
public class UnableToCreateReviewException extends RuntimeException {

    /**
     * Creates a new instance of <code>UnableToCreateReviewException</code>
     * without detail message.
     */
    public UnableToCreateReviewException() {
    }

    /**
     * Constructs an instance of <code>UnableToCreateReviewException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public UnableToCreateReviewException(String msg) {
        super(msg);
    }
}
