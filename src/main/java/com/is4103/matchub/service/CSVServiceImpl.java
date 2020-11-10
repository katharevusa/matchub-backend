/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.helper.CSVHelper;
import com.is4103.matchub.repository.AccountEntityRepository;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ngjin
 */
@Service
public class CSVServiceImpl implements CSVService {

    @Autowired
    private AccountEntityRepository accountEntityRepository;

    @Override
    public void importIndividuals(MultipartFile file) {
        if (CSVHelper.hasCSVFormat(file)) {
            try {

                List<IndividualEntity> individuals = CSVHelper.csvToIndividuals(file.getInputStream());
                accountEntityRepository.saveAll(individuals);

            } catch (IOException e) {
                throw new RuntimeException("fail to store csv data: " + e.getMessage());
            }
        } else {
            System.out.println("Please upload a csv file");
        }

    }
    
    @Override
    public void importOrganisations(MultipartFile file) {
        if (CSVHelper.hasCSVFormat(file)) {
            try {

                List<OrganisationEntity> organisations = CSVHelper.csvToOrganisations(file.getInputStream());
                accountEntityRepository.saveAll(organisations);

            } catch (IOException e) {
                throw new RuntimeException("fail to store csv data: " + e.getMessage());
            }
        } else {
            System.out.println("Please upload a csv file");
        }

    }

}
