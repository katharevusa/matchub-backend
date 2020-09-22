package com.is4103.matchub.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;

/**
 *
 * @author tjle2
 */
@Service
public class FirebaseServiceImpl implements FirebaseService {

    @PostConstruct
    public void initialize() {
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(new File(".").getCanonicalPath() + "/src/main/resources/matchub-ff3a7-88b25c8fa235.json"))
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setServiceAccountId("firebase-adminsdk-3akk0@matchub-ff3a7.iam.gserviceaccount.com")
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String issueFirebaseCustomToken(UUID uuid) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().createCustomToken(uuid.toString());
    }
}
