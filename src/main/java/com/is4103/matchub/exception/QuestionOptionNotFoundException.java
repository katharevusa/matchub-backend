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
public class QuestionOptionNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>QuestionOptionNotFoundException</code>
     * without detail message.
     */
    public QuestionOptionNotFoundException() {
    }

    /**
     * Constructs an instance of <code>QuestionOptionNotFoundException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public QuestionOptionNotFoundException(String msg) {
        super(msg);
    }
}
