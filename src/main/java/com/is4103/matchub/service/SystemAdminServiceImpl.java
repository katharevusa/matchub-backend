/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.repository.ProfileEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author longluqian
 */
@Service
public class SystemAdminServiceImpl implements SystemAdminService{
    @Autowired
    ProfileEntityRepository profileEntityRepository;
    
//    public  getStatistics(){
//        
//    }
    
}
