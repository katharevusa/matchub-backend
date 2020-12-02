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
public class ImportDataException extends RuntimeException {

    /**
     * Creates a new instance of <code>ImportDataException</code> without detail
     * message.
     */
    public ImportDataException() {
    }

    /**
     * Constructs an instance of <code>ImportDataException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ImportDataException(String msg) {
        super(msg);
    }
}
