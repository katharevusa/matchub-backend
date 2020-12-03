/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.helper.ImportDataErrorWrapper;
import com.is4103.matchub.service.DataMappingService;
import java.io.IOException;
import javax.mail.MessagingException;
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
    ImportDataErrorWrapper importIndividuals(@RequestParam(value = "file") MultipartFile file) throws IOException {
        return dataMappingService.importIndividuals(file);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/importOrganisations")
    ImportDataErrorWrapper importOrganisations(@RequestParam(value = "file") MultipartFile file) throws IOException {
        return dataMappingService.importOrganisations(file);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/template_individual")
    String retrieveCommonTemplateIndividual() {
        return dataMappingService.retrieveCommonTemplateIndividual();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/template_organisation")
    String retrieveCommonTemplateOrganisation() {
        return dataMappingService.retrieveCommonTemplateOrganisation();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/importIndividualsSendEmail")
    ImportDataErrorWrapper importIndividualsSendEmail(@RequestParam(value = "file") MultipartFile file) throws MessagingException, IOException {
        return dataMappingService.importIndividualsSendEmail(file);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/importOrganisationsSendEmail")
    ImportDataErrorWrapper importOrganisationsSendEmail(@RequestParam(value = "file") MultipartFile file) throws MessagingException, IOException {
        return dataMappingService.importOrganisationsSendEmail(file);
    }

}
