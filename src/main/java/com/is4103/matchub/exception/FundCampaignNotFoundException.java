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
public class FundCampaignNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>FundCampaignNotFoundException</code>
     * without detail message.
     */
    public FundCampaignNotFoundException() {
    }

    /**
     * Constructs an instance of <code>FundCampaignNotFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public FundCampaignNotFoundException(String msg) {
        super(msg);
    }
}
