package com.is4103.matchub.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.is4103.matchub.service.FirebaseService;
import com.is4103.matchub.vo.SendNotificationsToUsersVO;
import java.util.concurrent.ExecutionException;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tjle2
 */
@RestController
@RequestMapping("/authenticated")
public class FirebaseController {

    @Autowired
    FirebaseService firebaseService;

    @RequestMapping(method = RequestMethod.POST, value = "/sendNotificationsToUsers")
    void sendNotificationsToUsers(@Valid @RequestBody SendNotificationsToUsersVO vo) throws InterruptedException, ExecutionException, FirebaseMessagingException {
        firebaseService.sendNotificationsToUsers(vo);
    }
}
