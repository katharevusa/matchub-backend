package com.is4103.matchub.controller;

import com.is4103.matchub.service.UserService;
import com.is4103.matchub.vo.IndividualCreateVO;
import com.is4103.matchub.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.validation.Valid;


@RestController
@RequestMapping("/public")
public class PublicRestController {

    @Autowired
    UserService userService;

    @RequestMapping(method = RequestMethod.POST)
    UserVO create(@Valid @RequestBody IndividualCreateVO createVO) {
        return userService.create(createVO);
    }
}
