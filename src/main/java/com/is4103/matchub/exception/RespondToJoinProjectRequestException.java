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
public class RespondToJoinProjectRequestException extends Exception {

    /**
     * Creates a new instance of
     * <code>RespondToJoinProjectRequestException</code> without detail message.
     */
    public RespondToJoinProjectRequestException() {
    }

    /**
     * Constructs an instance of
     * <code>RespondToJoinProjectRequestException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public RespondToJoinProjectRequestException(String msg) {
        super(msg);
    }
}
