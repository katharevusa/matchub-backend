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
public class EmailExistException extends RuntimeException {

    public EmailExistException(String email) {
        super("Email is already used: " + email);
    }
}
