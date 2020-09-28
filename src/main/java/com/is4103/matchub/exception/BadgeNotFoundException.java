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
public class BadgeNotFoundException extends RuntimeException {

    /**
     * Creates a new instance of <code>BadgeNotFoundException</code> without
     * detail message.
     */
    public BadgeNotFoundException() {
    }

    /**
     * Constructs an instance of <code>BadgeNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public BadgeNotFoundException(String msg) {
        super(msg);
    }
}
