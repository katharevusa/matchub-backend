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
public class CreateAnnouncementException extends Exception {

    /**
     * Creates a new instance of <code>CreateAnnouncementException</code>
     * without detail message.
     */
    public CreateAnnouncementException() {
    }

    /**
     * Constructs an instance of <code>CreateAnnouncementException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateAnnouncementException(String msg) {
        super(msg);
    }
}
