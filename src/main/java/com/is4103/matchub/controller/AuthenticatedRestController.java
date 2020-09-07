package com.is4103.matchub.controller;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.service.UserService;
import com.is4103.matchub.vo.IndividualCreateVO;
import com.is4103.matchub.vo.UserVO;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.mail.MessagingException;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authenticated")
public class AuthenticatedRestController {

    @Autowired
    UserService userService;

    @RequestMapping(method = RequestMethod.GET, value = "/me")
    AccountEntity getMe(Principal principal) {
        return userService.getAccount(principal.getName());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAccount/{id}")
    AccountEntity getAccount(@PathVariable Long id) {
        return userService.getAccount(id);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllFollowingAccounts/{id}")
    List<AccountEntity> getAllFollowingAccounts(@PathVariable Long id) {
        return userService.getAllFollowingAccounts(id);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllFollowerAccounts/{id}")
    List<AccountEntity> getAllFollowerAccounts(@PathVariable Long id) {
        return userService.getAllFollowingAccounts(id);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteAccount/{id}")
    void deleteAccount(@PathVariable Long id) {
        userService.delete(id);
    }
}
