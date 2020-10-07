package com.is4103.matchub.service;

import com.is4103.matchub.vo.SendNotificationsToUsersVO;
import java.util.UUID;

/**
 *
 * @author tjle2
 */
public interface FirebaseService {

    public String issueFirebaseCustomToken(UUID uuid);

    public void sendNotificationsToUsers(SendNotificationsToUsersVO vo);
}
