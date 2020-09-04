/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.repository.AccountEntityRepository;
import java.io.IOException;
import java.util.Optional;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 *
 * @author ngjin
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private AccountEntityRepository accountEntityRepository;

    @Override
    public void sendActivationEmail(String to) throws MessagingException, IOException {
        SimpleMailMessage message = new SimpleMailMessage();

        //find the user uuid
        AccountEntity newRegisteredAccount = accountEntityRepository.findByEmail(to).get();

        String name = "";

        if (newRegisteredAccount instanceof IndividualEntity) {
            IndividualEntity i = (IndividualEntity) newRegisteredAccount;
            name = i.getFirstName() + " " + i.getLastName();
        } else if (newRegisteredAccount instanceof OrganisationEntity) {
            OrganisationEntity o = (OrganisationEntity) newRegisteredAccount;
            name = o.getOrganizationName();
        }

        String subject = "Activate Your MatcHub Account";

        String body = "Dear " + name + ", " + "\n\nWelcome to MatcHub! \nYou have successfully registered for a new account with us.\n"
                + "Please click on this unique activation link to activate and set up your profile: ";
        
        if (newRegisteredAccount instanceof IndividualEntity) {
            body += "http://localhost:3000/setupIndividualProfile/" + newRegisteredAccount.getUuid();
        } else { //must be an organisation
            body += "http://localhost:3000/setupOrganisationProfile/" + newRegisteredAccount.getUuid();
        }
        
        body += "\n\nThank you!\n\nRegards,\nMatcHub";

        message.setFrom("matchubcommunity@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        emailSender.send(message);
    }
}
