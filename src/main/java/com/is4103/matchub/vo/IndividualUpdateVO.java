/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.IndividualEntity;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author ngjin
 */
@Data
public class IndividualUpdateVO {

    @NotNull(message = "id can not be null.")
    private Long id;

    private String phoneNumber;

    private String country;

    private String city;

    private String firstName;

    private String lastName;

    private String profileDescription;

    private Set<String> skillSet = new HashSet<>();

    private Long[] sdgIds;

    public void updateIndividualAccount(IndividualEntity individual) {

        if (!this.phoneNumber.isEmpty()) {
            individual.setPhoneNumber(this.phoneNumber);
        }

        if (!this.country.isEmpty()) {
            individual.setCountry(this.country);
        }

        if (!this.city.isEmpty()) {
            individual.setCity(this.city);
        }

        if (!this.firstName.isEmpty()) {
            individual.setFirstName(this.firstName);
        }

        if (!this.lastName.isEmpty()) {
            individual.setLastName(this.lastName);
        }

        if (!this.profileDescription.isEmpty()) {
            individual.setProfileDescription(this.profileDescription);
        }

        if (!this.skillSet.isEmpty()) {
            //clear all the previous skillsets first
            individual.getSkillSet().clear();
            individual.setSkillSet(this.skillSet);
        }

        //do not associate udpatedSdg here, associate in user service class
    }

}
