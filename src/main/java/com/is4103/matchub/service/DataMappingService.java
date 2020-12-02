/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.helper.ImportDataErrorWrapper;
import java.io.IOException;
import javax.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ngjin
 */
public interface DataMappingService {

    ImportDataErrorWrapper importIndividuals(MultipartFile file) throws IOException;

    ImportDataErrorWrapper importOrganisations(MultipartFile file) throws IOException;

    ImportDataErrorWrapper importIndividualsSendEmail(MultipartFile file) throws MessagingException, IOException;

    ImportDataErrorWrapper importOrganisationsSendEmail(MultipartFile file) throws MessagingException, IOException;

    String retrieveCommonTemplateIndividual();

    String retrieveCommonTemplateOrganisation();
}
