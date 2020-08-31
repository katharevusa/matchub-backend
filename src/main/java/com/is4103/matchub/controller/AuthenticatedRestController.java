package com.is4103.matchub.controller;

import com.is4103.matchub.service.UserService;
import com.is4103.matchub.vo.UserVO;
import java.security.Principal;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authenticated")
public class AuthenticatedRestController {

    @Autowired
    UserService userService;

   
    @RequestMapping(method = RequestMethod.GET, value = "/me")
    UserVO getMe(Principal principal) {
        return userService.get(principal.getName());
    }
}
