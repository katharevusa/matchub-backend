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
public class DeleteFundCampaignException extends Exception {

    /**
     * Creates a new instance of <code>DeleteFundCampaignException</code>
     * without detail message.
     */
    public DeleteFundCampaignException() {
    }

    /**
     * Constructs an instance of <code>DeleteFundCampaignException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteFundCampaignException(String msg) {
        super(msg);
    }
}
