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
public class ResourceCategoryNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>ResourceCategoryNotFoundException</code>
     * without detail message.
     */
    public ResourceCategoryNotFoundException() {
    }

    /**
     * Constructs an instance of <code>ResourceCategoryNotFoundException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ResourceCategoryNotFoundException(String msg) {
        super(msg);
    }
}
