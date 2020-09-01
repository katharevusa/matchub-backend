package com.is4103.matchub.vo;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.enumeration.GenderEnum;
import com.is4103.matchub.validation.StringArrayEnum;
import com.is4103.matchub.validation.ValueOfEnum;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Enumerated;

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

    @NotNull(message = "username can not be null.")
    @NotBlank(message = "username can not be blank.")
    private String username;

    @NotNull(message = "password can not be null.")
    @NotBlank(message = "password can not be blank.")
    @Size(min = 8)
    private String password;

    @NotNull(message = "email can not be null.")
    @NotBlank(message = "email can not be blank.")
    @Email(message = "email must be valid.")
    private String email;

    @NotNull(message = "role can not be null.")
    @StringArrayEnum(message = "role is not valid", values = {AccountEntity.ROLE_ADMIN, AccountEntity.ROLE_USER})
    private String[] roles;

    public void updateAccount(AccountEntity account, PasswordEncoder passwordEncoder) {
        account.setUsername(this.username.trim());
        account.setPassword(passwordEncoder.encode(this.password));
        account.setEmail(this.email);
        account.setUuid(UUID.randomUUID());
        account.getRoles().clear();
        account.getRoles().addAll(Arrays.asList(this.roles));
    }
}
