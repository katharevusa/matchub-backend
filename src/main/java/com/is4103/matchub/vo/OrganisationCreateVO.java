/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.enumeration.GenderEnum;
import com.is4103.matchub.validation.StringArrayEnum;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author ngjin
 */
@Data
public class OrganisationCreateVO {

    @NotNull(message = "Organisation name can not be null.")
    @NotBlank(message = "Organisation name can not be blank.")
    private String organisationName;

    @NotNull(message = "email can not be null.")
    @NotBlank(message = "email can not be blank.")
    @Email(message = "email must be valid.")
    private String email;

    @NotNull(message = "password can not be null.")
    @NotBlank(message = "password can not be blank.")
    @Size(min = 8)
    private String password;

    @NotNull(message = "role can not be null.")
    @StringArrayEnum(message = "role is not valid", values = {AccountEntity.ROLE_SUPERUSER, AccountEntity.ROLE_SYSADMIN, AccountEntity.ROLE_USER})
    private String[] roles;

    public void updateOrganisationAccount(OrganisationEntity newOrg, PasswordEncoder passwordEncoder) {
        newOrg.setPassword(passwordEncoder.encode(this.password));
        newOrg.setEmail(this.email);
        newOrg.setUuid(UUID.randomUUID());
        newOrg.setJoinDate(LocalDateTime.now());
        newOrg.getRoles().clear();
        newOrg.getRoles().addAll(Arrays.asList(this.roles));

        newOrg.setOrganizationName(this.organisationName);
    }
}
