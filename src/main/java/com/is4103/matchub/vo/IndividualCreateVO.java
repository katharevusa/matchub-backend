/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.enumeration.GenderEnum;
import com.is4103.matchub.validation.StringArrayEnum;
import com.is4103.matchub.validation.ValueOfEnum;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.UUID;
import java.util.Arrays;
import lombok.Data;

/**
 *
 * @author ngjin
 */
@Data
public class IndividualCreateVO {

    @NotNull(message = "first name can not be null.")
    @NotBlank(message = "first name can not be blank.")
    private String firstName;

    @NotNull(message = "last name can not be null.")
    @NotBlank(message = "last name can not be blank.")
    private String lastName;

    @ValueOfEnum(enumClass = GenderEnum.class)
    @NotNull(message = "gender can not be null.")
    private String genderEnum;

    @Column(nullable = true)
    private String profileDescription;

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

    public void updateIndividualAccount(IndividualEntity newIndividual, PasswordEncoder passwordEncoder) {
        newIndividual.setPassword(passwordEncoder.encode(this.password));
        newIndividual.setEmail(this.email);
        newIndividual.setUuid(UUID.randomUUID());
        newIndividual.setJoinDate(LocalDateTime.now());
        newIndividual.getRoles().clear();
        newIndividual.getRoles().addAll(Arrays.asList(this.roles));

        newIndividual.setFirstName(this.firstName);
        newIndividual.setLastName(this.lastName);
        newIndividual.setGenderEnum(GenderEnum.valueOf(this.genderEnum));
        newIndividual.setProfileDescription(this.profileDescription);
    }

}
