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
public class DeleteSurveyException extends Exception {

    /**
     * Creates a new instance of <code>DeleteSurveyException</code> without
     * detail message.
     */
    public DeleteSurveyException() {
    }

    /**
     * Constructs an instance of <code>DeleteSurveyException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteSurveyException(String msg) {
        super(msg);
    }
}
