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
public class CreateFundCampaignException extends Exception {

    /**
     * Creates a new instance of <code>CreateFundCampaignException</code>
     * without detail message.
     */
    public CreateFundCampaignException() {
    }

    /**
     * Constructs an instance of <code>CreateFundCampaignException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateFundCampaignException(String msg) {
        super(msg);
    }
}
