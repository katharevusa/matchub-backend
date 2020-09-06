/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import java.io.IOException;
import javax.mail.MessagingException;

/**
 *
 * @author ngjin
 */
public interface EmailService {

    public void sendVerificationEmail(String to) throws MessagingException, IOException;
}
