/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.CompetitionEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.VoterCredentialEntity;
import java.io.IOException;
import javax.mail.MessagingException;

/**
 *
 * @author ngjin
 */
public interface EmailService {

    void sendVerificationEmail(AccountEntity newRegisteredAccount) throws MessagingException, IOException;

    void sendResetPasswordEmail(AccountEntity account) throws MessagingException, IOException;

    public void sendCompetitionEmailAlert(AccountEntity account, CompetitionEntity competition) throws MessagingException, IOException;

    public void sendVoterRegisterEmail(IndividualEntity newIndividual, String randomGeneratedPassword, VoterCredentialEntity voterCredential, CompetitionEntity competition) throws MessagingException, IOException;

    public void sendExistingUserVotingDetailsEmail(ProfileEntity profile, VoterCredentialEntity voterCredential, CompetitionEntity competition) throws MessagingException, IOException;

    void sendOnboardingEmail(ProfileEntity profile, String randomGeneratedPassword) throws MessagingException, IOException;
}
