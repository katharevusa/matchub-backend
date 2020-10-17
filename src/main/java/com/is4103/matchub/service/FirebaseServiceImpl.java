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
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.ApnsFcmOptions;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.exception.FirebaseRuntimeException;
import com.is4103.matchub.repository.AccountEntityRepository;
import com.is4103.matchub.vo.ChannelDetailsVO;
import com.is4103.matchub.vo.ChannelMappingVO;
import com.is4103.matchub.vo.FirebaseUserPojo;
import com.is4103.matchub.vo.SendNotificationsToUsersVO;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author tjle2
 */
@Service
public class FirebaseServiceImpl implements FirebaseService {

    @Autowired
    private AccountEntityRepository accountEntityRepository;

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
    public String issueFirebaseCustomToken(UUID uuid) {
        try {
            return FirebaseAuth.getInstance().createCustomToken(uuid.toString());
        } catch (FirebaseAuthException ex) {
            throw new FirebaseRuntimeException(ex.getMessage());
        }
    }

    @Override
    public void sendNotificationsToUsers(SendNotificationsToUsersVO vo) {

        try {
            CollectionReference users = FirestoreClient.getFirestore().collection("users");
            Query query = users.whereIn("uid", vo.getUuids());
            ApiFuture<QuerySnapshot> querySnapshot = query.get();

            List<String> webDeviceTokens = new ArrayList<>();
            List<String> mobileDeviceTokens = new ArrayList<>();

            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                FirebaseUserPojo userDetails = document.toObject(FirebaseUserPojo.class);
                if (userDetails.getMobilePushToken() != null) {
                    mobileDeviceTokens.add(userDetails.getMobilePushToken());
                }
                if (userDetails.getWebPushToken() != null) {
                    webDeviceTokens.add(userDetails.getWebPushToken());
                }
            }

            int messageCount = 0;

            if (webDeviceTokens.size() > 0) {
                MulticastMessage webMessage = MulticastMessage.builder()
                        .putData("type", vo.getType())
                        .putData("title", vo.getTitle())
                        .putData("body", vo.getBody())
                        .putData("image", vo.getImage())
                        .addAllTokens(webDeviceTokens)
                        .build();

                BatchResponse webResponse = FirebaseMessaging.getInstance().sendMulticast(webMessage);
                messageCount += webResponse.getSuccessCount();
            }

            if (mobileDeviceTokens.size() > 0) {
                MulticastMessage mobileMessage = MulticastMessage.builder()
                        .setNotification(Notification.builder()
                                .setTitle(vo.getTitle())
                                .setBody(vo.getBody())
                                .setImage(vo.getImage())
                                .build())
                        .setApnsConfig(ApnsConfig.builder()
                                .putHeader("mutable-content", "1")
                                .setFcmOptions(ApnsFcmOptions.builder()
                                        .setImage(vo.getImage())
                                        .build())
                                .setAps(Aps.builder()
                                        .build())
                                .build())
                        .addAllTokens(mobileDeviceTokens)
                        .build();

                BatchResponse mobileResponse = FirebaseMessaging.getInstance().sendMulticast(mobileMessage);
                messageCount += mobileResponse.getSuccessCount();
            }

            System.out.println(messageCount + " messages were sent successfully");
        } catch (InterruptedException | ExecutionException | FirebaseMessagingException ex) {
            throw new FirebaseRuntimeException(ex.getMessage());
        }
    }

    // get channel admins
    @Override
    public ChannelDetailsVO getChannelDetails(String channelUid) {
        List<Long> admins = new ArrayList<>();
        List<Long> members = new ArrayList<>();
        ChannelDetailsVO vo = new ChannelDetailsVO();
        try {
            CollectionReference future = FirestoreClient.getFirestore().collection("channels");
            Query query = future.whereEqualTo("id", channelUid);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                ChannelMappingVO channelMappingVO = document.toObject(ChannelMappingVO.class);
                System.out.println("channelMappingVO"+channelMappingVO);
                System.out.println("get admins:"+ channelMappingVO.getAdmins());
                System.out.println("get members:"+ channelMappingVO.getMembers());
                for (String s : channelMappingVO.getAdmins()) {
                    AccountEntity user = accountEntityRepository.findByUuid(UUID.fromString(s)).get();
                    admins.add(user.getAccountId());
                }

                for (String s : channelMappingVO.getMembers()) {
                    AccountEntity user = accountEntityRepository.findByUuid(UUID.fromString(s)).get();
                    members.add(user.getAccountId());
                }

                AccountEntity creator = accountEntityRepository.findByUuid(UUID.fromString(channelMappingVO.getCreatedBy())).get();
                vo.setAdminIds(admins);
                vo.setMemberIds(members);
                vo.setCreatorId(creator.getAccountId());
                System.out.println("****" + vo.toString());
                return vo;
            }

        } catch (InterruptedException | ExecutionException ex) {
            System.err.println("error when getting channel details " + ex.getMessage());
        }

        return vo;
    }

}
