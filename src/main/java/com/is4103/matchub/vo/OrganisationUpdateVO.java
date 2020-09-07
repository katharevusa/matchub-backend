/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author ngjin
 */
@Data
public class OrganisationUpdateVO {

    @NotNull(message = "id can not be null.")
    private Long id;

    private String password;

    private String phoneNumber;

    private String country;

    private String city;

    private String organizationName;

    private String organizationDescription;

    private String address;

    private Set<String> areasOfExpertise = new HashSet<>();

    public void updateOrganisationAccount(OrganisationEntity organisation, PasswordEncoder passwordEncoder) {
        if (!this.password.isEmpty() && this.password.length() >= 8) {
            organisation.setPassword(passwordEncoder.encode(this.password));
        }
        
        if (!this.phoneNumber.isEmpty()) {
            organisation.setPhoneNumber(this.phoneNumber);
        }
        
        if (!this.country.isEmpty()) {
            organisation.setCountry(this.country);
        }
        
        if (!this.city.isEmpty()) {
            organisation.setCity(this.city);
        }
        
        if (!this.organizationName.isEmpty()) {
            organisation.setOrganizationName(this.organizationName);
        }
        
        if (!this.organizationDescription.isEmpty()) {
            organisation.setOrganizationDescription(this.organizationDescription);
        }
        
        if (!this.address.isEmpty()) {
            organisation.setAddress(this.address);
        }
        
        if (!this.areasOfExpertise.isEmpty()) {
            //clear all the previous areas of expertise first
            organisation.getAreasOfExpertise().clear();
            organisation.setAreasOfExpertise(this.areasOfExpertise);
        }
    }
}
