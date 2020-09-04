/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.service.EmailServiceImpl;
import java.io.IOException;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ngjin
 */
@RestController
public class EmailController {

    @Autowired
    private EmailServiceImpl emailService;

    @RequestMapping("/sendActivationEmail")
    public String send() {
        System.out.println("Sending Email...");
        try {
            emailService.sendActivationEmail("ngjingwenjw@gmail.com");
        } catch (MessagingException e) {
            System.out.println("MessagingException here");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException here");
            e.printStackTrace();
        }

        System.out.println("Done");

        return "email sent successfully";
    }
}
