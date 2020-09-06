/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.SDGEntity;
import com.is4103.matchub.enumeration.GenderEnum;
import com.is4103.matchub.validation.ValueOfEnum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author ngjin
 */
@Data
public class IndividualSetupVO {

    @Column(nullable = true)
    private String profileDescription;

    private String[] skillSet;

    private Long[] projectFollowing;
    
    @ValueOfEnum(enumClass = GenderEnum.class)
    @NotNull(message = "gender can not be null.")
    private String genderEnum;

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

    private Long[] sdgIds;

    public void setupIndividualAccount(IndividualEntity individual) {
        individual.setProfileDescription(this.profileDescription);

        //convert array to set
        Set<String> stringSet = new HashSet<>(Arrays.asList(this.skillSet));
        individual.getSkillSet().clear();
        individual.setSkillSet(stringSet);

        //convert array to set
        Set<Long> longSet = new HashSet<>(Arrays.asList(this.projectFollowing));
        individual.getProjectFollowing().clear();
        individual.setProjectFollowing(longSet);
        
        individual.setGenderEnum(GenderEnum.valueOf(this.genderEnum));

        individual.setPhoneNumber(this.phoneNumber);
        individual.setCountry(this.country);
        individual.setCity(this.city);

        //convert array to set
        longSet = new HashSet<>(Arrays.asList(this.following));
        individual.getFollowing().clear();
        individual.setFollowing(longSet);

        //upload profile pic in attachment service class
        //do not associate sdg here, associate in user service class
    }

}
