/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ngjin
 */
@RestController
public class SetupProfileController {

    @RequestMapping("/setup/IndividualProfile")
    public void setupIndividualProfile() {
        //take in VO 
//        set assocaitions - sdg/skillsets etc in the service class
//make verified true 
    }

    @RequestMapping("/setup/OrganisationProfile")
    public void setupOrganisationProfile() {

    }
}
