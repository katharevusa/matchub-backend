/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.enumeration.AnnouncementTypeEnum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author ngjin
 */
@Data
public class OrganisationSetupVO {

    @NotNull(message = "Organisation description can not be null.")
    @NotBlank(message = "Organisation description can not be blank.")
    private String organizationDescription;

    @NotNull(message = "Address can not be null.")
    @NotBlank(message = "Address can not be blank.")
    private String address;

    private Long[] employees;

    private Long[] kahs;

    private String[] areasOfExpertise;
    
    @NotNull(message = "Country Code can not be null.")
    @NotBlank(message = "Country Code can not be blank.")
    private String countryCode;

    @NotNull(message = "Phone number can not be null.")
    @NotBlank(message = "Phone number can not be blank.")
    private String phoneNumber;

    @NotNull(message = "Country can not be null.")
    @NotBlank(message = "Country can not be blank.")
    private String country;

    @NotNull(message = "City can not be null.")
    @NotBlank(message = "City can not be blank.")
    private String city;

    private Long[] following;

//    private Long[] sdgIds;
    
    //*****************REFACTORED 
    // key: sdgId 
    // value: List of sdgTargetIds that belongs to the sdgId
    private Map<Long, List<Long>> hashmapSDG = new HashMap<>();

    public void setupOrganisationAccount(OrganisationEntity organisation) {
        organisation.setOrganizationDescription(this.organizationDescription);
        organisation.setAddress(this.address);

        //convert array to set
        Set<Long> longSet = new HashSet<>(Arrays.asList(this.employees));
        organisation.getEmployees().clear();
        organisation.setEmployees(longSet);

        //convert array to set
        longSet = new HashSet<>(Arrays.asList(this.kahs));
        organisation.getKahs().clear();
        organisation.setKahs(longSet);
        
        //convert array to set
        Set<String> stringSet = new HashSet<>(Arrays.asList(this.areasOfExpertise));
        organisation.getAreasOfExpertise().clear();
        organisation.setAreasOfExpertise(stringSet);
        
        organisation.setCountryCode(this.countryCode);
        organisation.setPhoneNumber(this.phoneNumber);
        organisation.setCountry(this.country);
        organisation.setCity(this.city);
        
        //convert array to set
        longSet = new HashSet<>(Arrays.asList(this.following));
        organisation.getFollowing().clear();
        organisation.setFollowing(longSet);

        //upload profile pic in attachment service class
        //do not associate sdg here, associate in user service class
        //set the announcement setting, default
        for(AnnouncementTypeEnum a : AnnouncementTypeEnum.values()){
            organisation.getAnnouncementsSetting().put(a, Boolean.TRUE);
        }
    }
}
