package com.is4103.matchub.controller;

import com.is4103.matchub.service.AttachmentService;
import com.is4103.matchub.service.UserService;
import com.is4103.matchub.vo.UserVO;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/authenticated")
public class AuthenticatedRestController {

    @Autowired
    UserService userService;

    @Autowired
    AttachmentService attachmentService;
    
    @RequestMapping(method = RequestMethod.GET, value = "/me")
    UserVO getMe(Principal principal) {
        return userService.get(principal.getName());
    }
    
    
    @RequestMapping(method = RequestMethod.POST, value = "/uploadFile")
    public String uploadFile(@RequestParam(value = "file") MultipartFile files,
            @RequestParam(value = "directory", required = false) String directory) {
        return attachmentService.upload(files, directory);
    }
}
