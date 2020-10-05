package com.is4103.matchub.service;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.common.collect.Lists;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.is4103.matchub.vo.FirebaseUserPojo;
import com.is4103.matchub.vo.SendNotificationsToUsersVO;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
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

    @Override
    public void sendNotificationsToUsers(SendNotificationsToUsersVO vo) throws InterruptedException, ExecutionException, FirebaseMessagingException {

        CollectionReference users = FirestoreClient.getFirestore().collection("users");
        Query query = users.whereIn("uid", vo.getUuids());
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        List<String> deviceTokens = new ArrayList<>();

        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            FirebaseUserPojo userDetails = document.toObject(FirebaseUserPojo.class);
            if (userDetails.getMobilePushToken() != null) {
                deviceTokens.add(userDetails.getMobilePushToken());
            }
            if (userDetails.getWebPushToken() != null) {
                deviceTokens.add(userDetails.getWebPushToken());
            }
        }

        MulticastMessage message = MulticastMessage.builder()
                .putData("type", vo.getType())
//                .putData("chatId", vo.getChatId())
                .putData("title", vo.getTitle())
                .putData("body", vo.getBody())
                .putData("image", vo.getImage())
                .addAllTokens(deviceTokens)
                .build();

        BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);

        System.out.println(response.getSuccessCount() + " messages were sent successfully");
    }
}
