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
public class DeleteAnnouncementException extends Exception {

    /**
     * Creates a new instance of <code>DeleteAnnouncementException</code>
     * without detail message.
     */
    public DeleteAnnouncementException() {
    }

    /**
     * Constructs an instance of <code>DeleteAnnouncementException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteAnnouncementException(String msg) {
        super(msg);
    }
}