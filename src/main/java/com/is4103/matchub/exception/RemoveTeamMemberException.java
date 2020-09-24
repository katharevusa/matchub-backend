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
public class RemoveTeamMemberException extends Exception {

    /**
     * Creates a new instance of <code>RemoveTeamMemberException</code> without
     * detail message.
     */
    public RemoveTeamMemberException() {
    }

    /**
     * Constructs an instance of <code>RemoveTeamMemberException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public RemoveTeamMemberException(String msg) {
        super(msg);
    }
}
