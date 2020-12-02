/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.SDGEntity;
import com.is4103.matchub.entity.SDGTargetEntity;
import com.is4103.matchub.entity.SelectedTargetEntity;
import com.is4103.matchub.enumeration.GenderEnum;
import com.is4103.matchub.exception.ImportDataException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.helper.ImportDataErrorWrapper;
import com.is4103.matchub.helper.RandomAlphanumericString;
import com.is4103.matchub.repository.IndividualEntityRepository;
import com.is4103.matchub.repository.OrganisationEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.SDGEntityRepository;
import com.is4103.matchub.repository.SDGTargetEntityRepository;
import com.is4103.matchub.repository.SelectedTargetEntityRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.mail.MessagingException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ngjin
 */
@Service
public class DataMappingServiceImpl implements DataMappingService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private SDGEntityRepository sdgEntityRepository;

    @Autowired
    private ProfileEntityRepository profileEntityRepository;

    @Autowired
    private IndividualEntityRepository individualEntityRepository;

    @Autowired
    private OrganisationEntityRepository organisationEntityRepository;

    @Autowired
    private SelectedTargetEntityRepository selectedTargetEntityRepository;

    @Autowired
    private SDGTargetEntityRepository sDGTargetEntityRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public ImportDataErrorWrapper importIndividuals(MultipartFile file) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        int numFailures = 0;
        List<String> errorMessages = new ArrayList<>();

        for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
            try {
                if (index > 0) {

                    XSSFRow row = worksheet.getRow(index);

                    IndividualEntity newInd = new IndividualEntity();

                    //predefined
                    String[] roles = {AccountEntity.ROLE_USER};
                    newInd.getRoles().addAll(Arrays.asList(roles));

                    UUID uuid = UUID.randomUUID();
                    newInd.setUuid(uuid);

                    //set password to a default value first
                    newInd.setPassword(passwordEncoder.encode("password"));

                    //read in from excel sheet
                    //email 
                    //check if email field is present if not assign dummy value 
                    if (row.getCell(3) == null) {
                        newInd.setEmail(uuid.toString());
                    } else {
                        newInd.setEmail(row.getCell(3).getStringCellValue());
                    }

                    //country 
                    if (row.getCell(9) == null) {
                        newInd.setCountry("");
                    } else {
                        newInd.setCountry(row.getCell(9).getStringCellValue());
                    }

                    //city
                    if (row.getCell(10) == null) {
                        newInd.setCity("");
                    } else {
                        newInd.setCity(row.getCell(10).getStringCellValue());
                    }

                    //first name 
                    newInd.setFirstName(row.getCell(1).getStringCellValue());

                    //last name 
                    if (row.getCell(2) == null) {
                        newInd.setLastName("");
                    } else {
                        newInd.setLastName(row.getCell(2).getStringCellValue());
                    }

                    //gender - hardcoded to be MALE
                    newInd.setGenderEnum(GenderEnum.MALE);

                    //profile desc
                    if (row.getCell(5) == null) {
                        newInd.setProfileDescription("");
                    } else {
                        newInd.setProfileDescription(row.getCell(5).getStringCellValue());
                    }

                    //create the individual
                    individualEntityRepository.saveAndFlush(newInd);

                    //set the associations 
                    if (row.getCell(7) != null) {
                        //read in sdg value 
                        long sdgNumber = (long) row.getCell(7).getNumericCellValue();

                        //find the actual instance of the sdg
                        SDGEntity sdg = sdgEntityRepository.findBySdgId(sdgNumber);
                        newInd.getSdgs().add(sdg);

                        //read in sdg target value 
                        if (row.getCell(8) != null) {
                            String sdgTargetValue = row.getCell(8).getStringCellValue();

                            //split based on commas and trim                        
                            String[] result = Arrays.stream(sdgTargetValue.split(",")).map(String::trim).toArray(String[]::new);

                            List<Long> list = new ArrayList<>();
                            for (String s : result) {
                                //find the actual instance of the sdg target 
                                SDGTargetEntity sdgTarget = sDGTargetEntityRepository.findBySdgTargetNumbering(s);
                                list.add(sdgTarget.getSdgTargetId());
                            }

                            //create selectedTargets
                            associateSDGTargetsWithProfile(list, sdgNumber, newInd.getAccountId());
                        }

                    }

                    //save the updated changes of the individual into database 
                    individualEntityRepository.saveAndFlush(newInd);

                }
            } catch (Exception ex) {
                numFailures++;
                int error = index + 1;
                String message = "Unable to read in data from row " + error;
                errorMessages.add(message);
            }

        }

        ImportDataErrorWrapper errorWrapper = new ImportDataErrorWrapper();

        errorWrapper.setNumFailures(numFailures);
        int numSuccesses = worksheet.getPhysicalNumberOfRows() - numFailures - 1;
        errorWrapper.setNumSuccesses(numSuccesses);
        errorWrapper.setErrorMessages(errorMessages);

        return errorWrapper;
    }

    @Override
    public ImportDataErrorWrapper importOrganisations(MultipartFile file) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        int numFailures = 0;
        List<String> errorMessages = new ArrayList<>();

        for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
            try {
                if (index > 0) {

                    XSSFRow row = worksheet.getRow(index);

                    OrganisationEntity newOrg = new OrganisationEntity();

                    //predefined
                    String[] roles = {AccountEntity.ROLE_USER};
                    newOrg.getRoles().addAll(Arrays.asList(roles));

                    UUID uuid = UUID.randomUUID();
                    newOrg.setUuid(uuid);

                    //set password to a default value first
                    newOrg.setPassword(passwordEncoder.encode("password"));

                    //read in from excel sheet
                    //email 
                    //check if email field is present if not assign dummy value 
                    if (row.getCell(2) == null) {
                        newOrg.setEmail(uuid.toString());
                    } else {
                        newOrg.setEmail(row.getCell(2).getStringCellValue());
                    }

                    //country 
                    if (row.getCell(7) == null) {
                        newOrg.setCountry("");
                    } else {
                        newOrg.setCountry(row.getCell(7).getStringCellValue());
                    }

                    //city
                    if (row.getCell(8) == null) {
                        newOrg.setCity("");
                    } else {
                        newOrg.setCity(row.getCell(8).getStringCellValue());
                    }

                    //organisation name
                    newOrg.setOrganizationName(row.getCell(1).getStringCellValue());

                    //organisation description
                    if (row.getCell(3) == null) {
                        newOrg.setOrganizationDescription("");
                    } else {
                        newOrg.setOrganizationDescription(row.getCell(3).getStringCellValue());
                    }

                    //create the organisation
                    organisationEntityRepository.saveAndFlush(newOrg);

                    //set the associations 
                    if (row.getCell(5) != null) {
                        //read in sdg value 
                        long sdgNumber = (long) row.getCell(5).getNumericCellValue();

                        //find the actual instance of the sdg
                        SDGEntity sdg = sdgEntityRepository.findBySdgId(sdgNumber);
                        newOrg.getSdgs().add(sdg);

                        //read in sdg target value 
                        if (row.getCell(6) != null) {
                            String sdgTargetValue = row.getCell(6).getStringCellValue();

                            //split based on commas and trim                        
                            String[] result = Arrays.stream(sdgTargetValue.split(",")).map(String::trim).toArray(String[]::new);

                            List<Long> list = new ArrayList<>();
                            for (String s : result) {
                                //find the actual instance of the sdg target 
                                SDGTargetEntity sdgTarget = sDGTargetEntityRepository.findBySdgTargetNumbering(s);
                                list.add(sdgTarget.getSdgTargetId());
                            }

                            //create selectedTargets
                            associateSDGTargetsWithProfile(list, sdgNumber, newOrg.getAccountId());
                        }

                    }

                    //save the updated changes of the organisation into database 
                    organisationEntityRepository.saveAndFlush(newOrg);

                }
            } catch (Exception ex) {
                numFailures++;
                int error = index + 1;
                String message = "Unable to read in data from row " + error;
                errorMessages.add(message);
            }

        }

        ImportDataErrorWrapper errorWrapper = new ImportDataErrorWrapper();

        errorWrapper.setNumFailures(numFailures);
        int numSuccesses = worksheet.getPhysicalNumberOfRows() - numFailures - 1;
        errorWrapper.setNumSuccesses(numSuccesses);
        errorWrapper.setErrorMessages(errorMessages);

        return errorWrapper;
    }

    @Override
    public ImportDataErrorWrapper importIndividualsSendEmail(MultipartFile file) throws MessagingException, IOException {

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        int numFailures = 0;
        List<String> errorMessages = new ArrayList<>();

        for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
            try {
                if (index > 0) {

                    XSSFRow row = worksheet.getRow(index);

                    IndividualEntity newInd = new IndividualEntity();

                    //predefined
                    String[] roles = {AccountEntity.ROLE_USER};
                    newInd.getRoles().addAll(Arrays.asList(roles));

                    UUID uuid = UUID.randomUUID();
                    newInd.setUuid(uuid);

                    //set password to a default value first
                    String randomGeneratedPassword = RandomAlphanumericString.randomString(12);
                    newInd.setPassword(passwordEncoder.encode(randomGeneratedPassword));

                    //read in from excel sheet
                    //email 
                    //check if email field is present if not assign dummy value 
                    if (row.getCell(3) == null) {
                        newInd.setEmail(uuid.toString());
                    } else {
                        newInd.setEmail(row.getCell(3).getStringCellValue());
                    }

                    //country 
                    if (row.getCell(9) == null) {
                        newInd.setCountry("");
                    } else {
                        newInd.setCountry(row.getCell(9).getStringCellValue());
                    }

                    //city
                    if (row.getCell(10) == null) {
                        newInd.setCity("");
                    } else {
                        newInd.setCity(row.getCell(10).getStringCellValue());
                    }

                    //first name 
                    newInd.setFirstName(row.getCell(1).getStringCellValue());

                    //last name 
                    if (row.getCell(2) == null) {
                        newInd.setLastName("");
                    } else {
                        newInd.setLastName(row.getCell(2).getStringCellValue());
                    }

                    //gender - hardcoded to be MALE
                    newInd.setGenderEnum(GenderEnum.MALE);

                    //profile desc
                    if (row.getCell(5) == null) {
                        newInd.setProfileDescription("");
                    } else {
                        newInd.setProfileDescription(row.getCell(5).getStringCellValue());
                    }

                    //create the individual
                    individualEntityRepository.saveAndFlush(newInd);

                    //set the associations 
                    if (row.getCell(7) != null) {
                        //read in sdg value 
                        long sdgNumber = (long) row.getCell(7).getNumericCellValue();

                        //find the actual instance of the sdg
                        SDGEntity sdg = sdgEntityRepository.findBySdgId(sdgNumber);
                        newInd.getSdgs().add(sdg);

                        //read in sdg target value 
                        if (row.getCell(8) != null) {
                            String sdgTargetValue = row.getCell(8).getStringCellValue();

                            //split based on commas and trim                        
                            String[] result = Arrays.stream(sdgTargetValue.split(",")).map(String::trim).toArray(String[]::new);

                            List<Long> list = new ArrayList<>();
                            for (String s : result) {
                                //find the actual instance of the sdg target 
                                SDGTargetEntity sdgTarget = sDGTargetEntityRepository.findBySdgTargetNumbering(s);
                                list.add(sdgTarget.getSdgTargetId());
                            }

                            //create selectedTargets
                            associateSDGTargetsWithProfile(list, sdgNumber, newInd.getAccountId());
                        }

                    }

                    //save the updated changes of the individual into database 
                    individualEntityRepository.saveAndFlush(newInd);

                    //if email address is present, send onboarding email to address
                    if (row.getCell(3) != null && row.getCell(3).getStringCellValue().contains("@")) {

                        //set disable to false and isVerified to true
                        newInd.setDisabled(Boolean.FALSE);
                        newInd.setIsVerified(Boolean.TRUE);
                        newInd.setJoinDate(LocalDateTime.now());

                        individualEntityRepository.saveAndFlush(newInd);

                        //send email 
                        emailService.sendOnboardingEmail(newInd, randomGeneratedPassword);
                    }

                }
            } catch (Exception ex) {
                numFailures++;
                int error = index + 1;
                String message = "Unable to read in data from row " + error;
                errorMessages.add(message);
            }

        }

        ImportDataErrorWrapper errorWrapper = new ImportDataErrorWrapper();

        errorWrapper.setNumFailures(numFailures);
        int numSuccesses = worksheet.getPhysicalNumberOfRows() - numFailures - 1;
        errorWrapper.setNumSuccesses(numSuccesses);
        errorWrapper.setErrorMessages(errorMessages);

        return errorWrapper;
    }

    @Override
    public ImportDataErrorWrapper importOrganisationsSendEmail(MultipartFile file) throws MessagingException, IOException {

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        int numFailures = 0;
        List<String> errorMessages = new ArrayList<>();

        for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
            try {
                if (index > 0) {

                    XSSFRow row = worksheet.getRow(index);

                    OrganisationEntity newOrg = new OrganisationEntity();

                    //predefined
                    String[] roles = {AccountEntity.ROLE_USER};
                    newOrg.getRoles().addAll(Arrays.asList(roles));

                    UUID uuid = UUID.randomUUID();
                    newOrg.setUuid(uuid);

                    //set password to a random string 
                    String randomGeneratedPassword = RandomAlphanumericString.randomString(12);
                    newOrg.setPassword(passwordEncoder.encode(randomGeneratedPassword));

                    //read in from excel sheet
                    //email 
                    //check if email field is present if not assign dummy value 
                    if (row.getCell(2) == null) {
                        newOrg.setEmail(uuid.toString());
                    } else {
                        newOrg.setEmail(row.getCell(2).getStringCellValue());
                    }

                    //country 
                    if (row.getCell(7) == null) {
                        newOrg.setCountry("");
                    } else {
                        newOrg.setCountry(row.getCell(7).getStringCellValue());
                    }

                    //city
                    if (row.getCell(8) == null) {
                        newOrg.setCity("");
                    } else {
                        newOrg.setCity(row.getCell(8).getStringCellValue());
                    }

                    //organisation name
                    newOrg.setOrganizationName(row.getCell(1).getStringCellValue());

                    //organisation description
                    if (row.getCell(3) == null) {
                        newOrg.setOrganizationDescription("");
                    } else {
                        newOrg.setOrganizationDescription(row.getCell(3).getStringCellValue());
                    }

                    //create the organisation
                    organisationEntityRepository.saveAndFlush(newOrg);

                    //set the associations 
                    if (row.getCell(5) != null) {
                        //read in sdg value 
                        long sdgNumber = (long) row.getCell(5).getNumericCellValue();

                        //find the actual instance of the sdg
                        SDGEntity sdg = sdgEntityRepository.findBySdgId(sdgNumber);
                        newOrg.getSdgs().add(sdg);

                        //read in sdg target value 
                        if (row.getCell(6) != null) {
                            String sdgTargetValue = row.getCell(6).getStringCellValue();

                            //split based on commas and trim                        
                            String[] result = Arrays.stream(sdgTargetValue.split(",")).map(String::trim).toArray(String[]::new);

                            List<Long> list = new ArrayList<>();
                            for (String s : result) {
                                //find the actual instance of the sdg target 
                                SDGTargetEntity sdgTarget = sDGTargetEntityRepository.findBySdgTargetNumbering(s);
                                list.add(sdgTarget.getSdgTargetId());
                            }

                            //create selectedTargets
                            associateSDGTargetsWithProfile(list, sdgNumber, newOrg.getAccountId());
                        }

                    }

                    //save the updated changes of the organisation into database 
                    organisationEntityRepository.saveAndFlush(newOrg);

                    //if email address is present, send onboarding email to address
                    if (row.getCell(2) != null && row.getCell(2).getStringCellValue().contains("@")) {

                        //set disable to false and isVerified to true
                        newOrg.setDisabled(Boolean.FALSE);
                        newOrg.setIsVerified(Boolean.TRUE);
                        newOrg.setJoinDate(LocalDateTime.now());

                        organisationEntityRepository.saveAndFlush(newOrg);

                        //send email 
                        emailService.sendOnboardingEmail(newOrg, randomGeneratedPassword);
                    }

                }
            } catch (Exception ex) {
                numFailures++;
                int error = index + 1;
                String message = "Unable to read in data from row " + error;
                errorMessages.add(message);
            }

        }

        ImportDataErrorWrapper errorWrapper = new ImportDataErrorWrapper();

        errorWrapper.setNumFailures(numFailures);
        int numSuccesses = worksheet.getPhysicalNumberOfRows() - numFailures - 1;
        errorWrapper.setNumSuccesses(numSuccesses);
        errorWrapper.setErrorMessages(errorMessages);

        return errorWrapper;
    }

    private void associateSDGTargetsWithProfile(List<Long> sdgTargetIds, Long sdgId, Long accountId) {
        //find the sdg
        SDGEntity sdg = sdgEntityRepository.findBySdgId(sdgId);

        //find the list of sdgTargets instances
        List<SDGTargetEntity> sdgTargetlist = sDGTargetEntityRepository.findSDGTargetsByIds(sdgTargetIds);

        //find the profile
        ProfileEntity profile = profileEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));

        SelectedTargetEntity s = new SelectedTargetEntity();

        s.setProfile(profile);
        s.setSdg(sdg);
        s.getSdgTargets().addAll(sdgTargetlist);

        selectedTargetEntityRepository.saveAndFlush(s);

        //set bidirectional association
        profile.getSelectedTargets().add(s);
        profileEntityRepository.saveAndFlush(profile);
    }

    @Override
    public String retrieveCommonTemplateIndividual() {
        return "https://localhost:8443/api/v1/files/commonTemplate/Template_Individual.xlsx";
    }

    @Override
    public String retrieveCommonTemplateOrganisation() {
        return "https://localhost:8443/api/v1/files/commonTemplate/Template_Organisation.xlsx";
    }
}
