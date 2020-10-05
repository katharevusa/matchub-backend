package com.is4103.matchub.service;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.is4103.matchub.vo.SendNotificationsToUsersVO;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author tjle2
 */
public interface FirebaseService {

    public String issueFirebaseCustomToken(UUID uuid) throws FirebaseAuthException;

    public void sendNotificationsToUsers(SendNotificationsToUsersVO vo) throws InterruptedException, ExecutionException, FirebaseMessagingException;
}
