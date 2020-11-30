/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.SDGEntity;
import com.is4103.matchub.entity.SDGTargetEntity;
import com.is4103.matchub.entity.SelectedTargetEntity;
import com.is4103.matchub.enumeration.GenderEnum;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.IndividualEntityRepository;
import com.is4103.matchub.repository.OrganisationEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.SDGEntityRepository;
import com.is4103.matchub.repository.SDGTargetEntityRepository;
import com.is4103.matchub.repository.SelectedTargetEntityRepository;
import static edu.stanford.nlp.stats.Counters.product;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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

    @Override
    public void importIndividuals(MultipartFile file) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
            if (index > 0) {

                XSSFRow row = worksheet.getRow(index);

                IndividualEntity newInd = new IndividualEntity();

                //predefined
                String[] roles = {"AccountEntity.ROLE_USER"};
                newInd.getRoles().addAll(Arrays.asList(roles));

                newInd.setUuid(UUID.randomUUID());

                //set password to a default value first
                newInd.setPassword(passwordEncoder.encode("password"));

                //read in from excel sheet
                //email 
                newInd.setEmail(row.getCell(3).getStringCellValue());

                //country 
                if (row.getCell(9).getStringCellValue() == null) {
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
                newInd.setLastName(row.getCell(2).getStringCellValue());

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

                        //find the actual instance of the sdg target 
                        SDGTargetEntity sdgTarget = sDGTargetEntityRepository.findBySdgTargetNumbering(sdgTargetValue);

                        //create selectedTargets
                        associateSDGTargetsWithProfile(sdgTarget.getSdgTargetId(), sdgNumber, newInd.getAccountId());
                    }

                }

                //save the updated changes of the individual into database 
                individualEntityRepository.saveAndFlush(newInd);

            }
        }
    }

    private void associateSDGTargetsWithProfile(Long sdgTargetId, Long sdgId, Long accountId) {
        //find the sdg
        SDGEntity sdg = sdgEntityRepository.findBySdgId(sdgId);

        //find the list of sdgTargets instances
        SDGTargetEntity sdgTarget = sDGTargetEntityRepository.findBySdgTargetId(sdgTargetId);

        //find the profile
        ProfileEntity profile = profileEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));

        SelectedTargetEntity s = new SelectedTargetEntity();

        s.setProfile(profile);
        s.setSdg(sdg);
        s.getSdgTargets().add(sdgTarget);

        selectedTargetEntityRepository.saveAndFlush(s);

        //set bidirectional association
        profile.getSelectedTargets().add(s);
        profileEntityRepository.saveAndFlush(profile);
    }
}
