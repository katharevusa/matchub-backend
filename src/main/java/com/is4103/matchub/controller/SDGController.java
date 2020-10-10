/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.SDGEntity;
import com.is4103.matchub.service.SDGService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ngjin
 */
@RestController
@RequestMapping("/authenticated")
public class SDGController {
    
    @Autowired
    SDGService sdgService;
    
    @RequestMapping(method = RequestMethod.GET, value = "/getAllSdgs")
    public Page<SDGEntity> getAllSdgs(Pageable pageable) {
        return sdgService.getAllSdgs(pageable);
    }
}
