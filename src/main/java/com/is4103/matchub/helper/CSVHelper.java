/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.helper;

import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ngjin
 */
public class CSVHelper {

    public static String TYPE = "text/csv";

    public static boolean hasCSVFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }

    public static List<IndividualEntity> csvToIndividuals(InputStream is) {

        long t0 = System.currentTimeMillis();
        System.out.println("START reading csv (individual) *************** ");

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                CSVParser csvParser = new CSVParser(fileReader,
                        CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

            List<IndividualEntity> individuals = new ArrayList<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                IndividualEntity individual = new IndividualEntity();

                individual.setEmail(csvRecord.get("Contact details"));

                individual.setFirstName(csvRecord.get("First Name"));
                individual.setLastName(csvRecord.get("Last Name"));

                individual.setProfileDescription(csvRecord.get("About the organization"));

                individual.setCountry(csvRecord.get("Country"));
                individual.setCity(csvRecord.get("City"));

                //predefined
                String[] roles = {"AccountEntity.ROLE_USER"};
                individual.getRoles().addAll(Arrays.asList(roles));

                individual.setUuid(UUID.randomUUID());

                individuals.add(individual);
            }

            long t1 = System.currentTimeMillis();
            System.out.println("Done in " + (t1 - t0) + " msec.");

            return individuals;

        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    public static List<OrganisationEntity> csvToOrganisations(InputStream is) {

        long t0 = System.currentTimeMillis();
        System.out.println("START reading csv (organisation) *************** ");

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                CSVParser csvParser = new CSVParser(fileReader,
                        CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

            List<OrganisationEntity> organisations = new ArrayList<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                OrganisationEntity organisation = new OrganisationEntity();

//                organisation.setEmail(csvRecord.get("Email"));
                organisation.setCountry(csvRecord.get("Country"));
                organisation.setCity(csvRecord.get("City"));

                organisation.setOrganizationName(csvRecord.get("Organization name"));
                organisation.setOrganizationDescription(csvRecord.get("About the organization"));

                //predefined
                String[] roles = {"AccountEntity.ROLE_USER"};
                organisation.getRoles().addAll(Arrays.asList(roles));

                organisation.setUuid(UUID.randomUUID());

                organisations.add(organisation);
            }

            long t1 = System.currentTimeMillis();
            System.out.println("Done in " + (t1 - t0) + " msec.");

            return organisations;

        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

}
