package com.is4103.matchub.service;

import com.google.firebase.auth.FirebaseAuthException;
import java.util.UUID;

/**
 *
 * @author tjle2
 */
public interface FirebaseService {

    public String issueFirebaseCustomToken(UUID uuid) throws FirebaseAuthException;
}
