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
public class DeleteQuestionOptionException extends Exception {

    /**
     * Creates a new instance of <code>DeleteQuestionOptionException</code>
     * without detail message.
     */
    public DeleteQuestionOptionException() {
    }

    /**
     * Constructs an instance of <code>DeleteQuestionOptionException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteQuestionOptionException(String msg) {
        super(msg);
    }
}
