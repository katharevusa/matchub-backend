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
public class DeleteResourceCategoryException extends Exception {

    /**
     * Creates a new instance of <code>DeleteResourceCategoryException</code>
     * without detail message.
     */
    public DeleteResourceCategoryException() {
    }

    /**
     * Constructs an instance of <code>DeleteResourceCategoryException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteResourceCategoryException(String msg) {
        super(msg);
    }
}
