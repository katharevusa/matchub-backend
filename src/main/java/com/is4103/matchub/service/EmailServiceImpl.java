/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.ClaimRequestEntity;
import com.is4103.matchub.entity.CompetitionEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.VoterCredentialEntity;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;

/**
 *
 * @author ngjin
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Async
    @Override
    public void sendVerificationEmail(AccountEntity newRegisteredAccount) throws MessagingException, IOException {
        SimpleMailMessage message = new SimpleMailMessage();

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
        message.setTo(newRegisteredAccount.getEmail());
        message.setSubject(subject);
        message.setText(body);
        emailSender.send(message);
    }

    @Async
    @Override
    public void sendResetPasswordEmail(AccountEntity account) throws MessagingException, IOException {
        SimpleMailMessage message = new SimpleMailMessage();

        String name = "";

        if (account instanceof IndividualEntity) {
            IndividualEntity i = (IndividualEntity) account;
            name = i.getFirstName() + " " + i.getLastName();
        } else if (account instanceof OrganisationEntity) {
            OrganisationEntity o = (OrganisationEntity) account;
            name = o.getOrganizationName();
        }

        String subject = "Reset Your MatcHub Password";

        String body = "Dear " + name + ", " + "\n\nYou requested to reset your password for your "
                + "MatcHub account. \nClick on the link to reset it: ";

        body += "http://localhost:3000/resetPassword/" + account.getUuid();

        body += "\n\nThank you!\n\nRegards,\nMatcHub";

        message.setFrom("matchubcommunity@gmail.com");
        message.setTo(account.getEmail());
        message.setSubject(subject);
        message.setText(body);
        emailSender.send(message);
    }

    @Async
    @Override
    public void sendCompetitionEmailAlert(AccountEntity account, CompetitionEntity competition) throws MessagingException, IOException {
        SimpleMailMessage message = new SimpleMailMessage();

        String name = "";

        if (account instanceof IndividualEntity) {
            IndividualEntity i = (IndividualEntity) account;
            name = i.getFirstName() + " " + i.getLastName();
        } else if (account instanceof OrganisationEntity) {
            OrganisationEntity o = (OrganisationEntity) account;
            name = o.getOrganizationName();
        }

        DecimalFormat df = new DecimalFormat("#,###");

        String subject = "Stand a chance to win $" + df.format(competition.getPrizeMoney()) + " for your projects!";

        String body = "Dear " + name + ", " + "\n\nA fresh new competition has been launched on "
                + "MatcHub. \nVisit MatcHub today to enter: http://localhost:3000/login";

        body += "\n\nThank you!\n\nRegards,\nMatcHub";

        message.setFrom("matchubcommunity@gmail.com");

        // currently send to own account, to change to account.getEmail() if needed.
        message.setTo("tellybuddy3106@gmail.com");
        message.setSubject(subject);
        message.setText(body);
        emailSender.send(message);
    }

    @Async
    @Override
    public void sendVoterRegisterEmail(IndividualEntity newIndividual, String randomGeneratedPassword, VoterCredentialEntity voterCredential, CompetitionEntity competition) throws MessagingException, IOException {
        SimpleMailMessage message = new SimpleMailMessage();

        String subject = "Here are your Voting Details!";

        String body = "Dear " + newIndividual.getFirstName() + " " + newIndividual.getLastName() + ", " + "\n\nYou have successfully registered for a voting token for "
                + "MatcHub's latest competition: " + competition.getCompetitionTitle() + ".";

        body += "\n\nYour Voting Token is as follows: " + voterCredential.getVoterSecret();
        body += "\nBrowse the Projects on MatcHub today to vote for your favourite Project: http://localhost:3000/competitions/" + competition.getCompetitionId() + "/details";

        body += "\n\nWe have also created an account for you on MatcHub and we hope that you will join us on our platform!";
        body += "\n\nLogin Credentials:\n";
        body += "Username: " + newIndividual.getEmail();
        body += "\nPassword: " + randomGeneratedPassword;
        body += "\n\nVisit MatcHub today and setup your account: http://localhost:3000/setupIndividualProfile/" + newIndividual.getUuid();

        body += "\n\nThank you!\n\nRegards,\nMatcHub";

        message.setFrom("matchubcommunity@gmail.com");

        message.setTo(newIndividual.getEmail());
        message.setSubject(subject);
        message.setText(body);
        emailSender.send(message);
    }

    @Async
    @Override
    public void sendExistingUserVotingDetailsEmail(ProfileEntity profile, VoterCredentialEntity voterCredential, CompetitionEntity competition) throws MessagingException, IOException {
        SimpleMailMessage message = new SimpleMailMessage();

        String name = "";

        if (profile instanceof IndividualEntity) {
            IndividualEntity i = (IndividualEntity) profile;
            name = i.getFirstName() + " " + i.getLastName();
        } else if (profile instanceof OrganisationEntity) {
            OrganisationEntity o = (OrganisationEntity) profile;
            name = o.getOrganizationName();
        }

        String subject = "Here are your Voting Details!";

        String body = "Dear " + name + ", " + "\n\nYou have requested for voting token for "
                + "MatcHub's latest competition: " + competition.getCompetitionTitle() + ".";

        body += "\n\nYour Voting Token is as follows: " + voterCredential.getVoterSecret();
        body += "\nBrowse the Projects on MatcHub today to vote for your favourite Project: http://localhost:3000/competitions/" + competition.getCompetitionId() + "/details";

        body += "\n\nThank you!\n\nRegards,\nMatcHub";

        message.setFrom("matchubcommunity@gmail.com");

        message.setTo(profile.getEmail());
        message.setSubject(subject);
        message.setText(body);
        emailSender.send(message);
    }

    @Async
    @Override
    public void sendOnboardingEmail(ProfileEntity profile, String randomGeneratedPassword) throws MessagingException, IOException {
        SimpleMailMessage message = new SimpleMailMessage();

        String name = "";

        if (profile instanceof IndividualEntity) {
            IndividualEntity i = (IndividualEntity) profile;
            name = i.getFirstName() + " " + i.getLastName();
        } else if (profile instanceof OrganisationEntity) {
            OrganisationEntity o = (OrganisationEntity) profile;
            name = o.getOrganizationName();
        }

        String subject = "A MatcHub account has been created on your behalf!";

        String body = "Dear " + name + ", " + "\n\nYour data had been imported into Matchub. To simplify "
                + "your onboarding process, we have already imported your basic information.";

        body += "\n\nWe have generated your login credentials for your first time login. Please do change your password upon your first login.";
        body += "\n\nYour Login Credentials:\n";
        body += "Username: " + profile.getEmail();
        body += "\nPassword: " + randomGeneratedPassword;

        body += "\n\nWe can't wait to see you inside!";

        body += "\n\nThank you!\n\nRegards,\nMatcHub";

        message.setFrom("matchubcommunity@gmail.com");

        message.setTo(profile.getEmail());
        message.setSubject(subject);
        message.setText(body);
        emailSender.send(message);
    }

    @Async
    @Override
    public void sendClaimRequestSuccessEmail(AccountEntity accountToClaim, String randomGeneratedPassword) throws MessagingException, IOException {
        SimpleMailMessage message = new SimpleMailMessage();

        String name = "";

        if (accountToClaim instanceof IndividualEntity) {
            IndividualEntity i = (IndividualEntity) accountToClaim;
            name = i.getFirstName() + " " + i.getLastName();
        } else if (accountToClaim instanceof OrganisationEntity) {
            OrganisationEntity o = (OrganisationEntity) accountToClaim;
            name = o.getOrganizationName();
        }

        String subject = "MatcHub Profile Claim Success!";

        String body = "Dear " + name + ", " + "\n\nYour request for your account has successfully been approved.";

        body += "\n\nWe have generated your login credentials for your first time login. Please do change your password upon your first login.";
        body += "\n\nLogin Credentials:\n";
        body += "Username: " + accountToClaim.getEmail();
        body += "\nPassword: " + randomGeneratedPassword;
        body += "\n\nVisit MatcHub today and login now: http://localhost:3000/login";

        body += "\n\nWe can't wait to see you inside!";

        body += "\n\nThank you!\n\nRegards,\nMatcHub";

        message.setTo(accountToClaim.getEmail());
        message.setSubject(subject);
        message.setText(body);
        emailSender.send(message);
    }

    @Async
    @Override
    public void sendClaimRequestFailureEmail(ClaimRequestEntity claimRequest, AccountEntity accountToClaim) throws MessagingException, IOException {
        SimpleMailMessage message = new SimpleMailMessage();

        String name = "";

        if (accountToClaim instanceof IndividualEntity) {
            IndividualEntity i = (IndividualEntity) accountToClaim;
            name = i.getFirstName() + " " + i.getLastName();
        } else if (accountToClaim instanceof OrganisationEntity) {
            OrganisationEntity o = (OrganisationEntity) accountToClaim;
            name = o.getOrganizationName();
        }

        String subject = "MatcHub Profile Claim Failed";

        String body = "Dear User," + "\n\nYour claim request for " + name + "'s account was unsuccessful.";

        body += "\n\nInstead, do sign up for an account if you are interested to join us: http://localhost:3000/signUp";

        body += "\n\nThank you!\n\nRegards,\nMatcHub";

        message.setTo(claimRequest.getEmail());
        message.setSubject(subject);
        message.setText(body);
        emailSender.send(message);
    }
}
