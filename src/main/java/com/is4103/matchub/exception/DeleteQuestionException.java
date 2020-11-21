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
public class DeleteQuestionException extends Exception {

    /**
     * Creates a new instance of <code>DeleteQuestionException</code> without
     * detail message.
     */
    public DeleteQuestionException() {
    }

    /**
     * Constructs an instance of <code>DeleteQuestionException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteQuestionException(String msg) {
        super(msg);
    }
}
