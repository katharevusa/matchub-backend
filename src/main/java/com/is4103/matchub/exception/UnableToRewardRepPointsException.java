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
public class UnableToRewardRepPointsException extends RuntimeException {

    /**
     * Creates a new instance of <code>UnableToRewardRepPointsException</code>
     * without detail message.
     */
    public UnableToRewardRepPointsException() {
    }

    /**
     * Constructs an instance of <code>UnableToRewardRepPointsException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public UnableToRewardRepPointsException(String msg) {
        super(msg);
    }
}
