/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.service.DataMappingService;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ngjin
 */
@RestController
@RequestMapping("/public")
public class DataMappingController {

    @Autowired
    DataMappingService dataMappingService;

    @RequestMapping(method = RequestMethod.POST, value = "/importIndividuals")
    void importIndividuals(@RequestParam(value = "file") MultipartFile file) throws IOException {
        dataMappingService.importIndividuals(file);
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/importOrganisations")
    void importOrganisations(@RequestParam(value = "file") MultipartFile file) throws IOException {
        dataMappingService.importOrganisations(file);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/template_individual")
    void retrieveCommonTemplateIndividual() {
        dataMappingService.retrieveCommonTemplateIndividual();
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/template_organisation")
    void retrieveCommonTemplateOrganisation() {
        dataMappingService.retrieveCommonTemplateOrganisation();
    }

}
