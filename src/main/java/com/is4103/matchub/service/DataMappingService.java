/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ngjin
 */
public interface DataMappingService {

    void importIndividuals(MultipartFile file) throws IOException;

    void importOrganisations(MultipartFile file) throws IOException;

    String retrieveCommonTemplateIndividual();

    String retrieveCommonTemplateOrganisation();
}
