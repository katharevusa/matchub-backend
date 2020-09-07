/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.AccountEntity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author ngjin
 */
public class ResetPasswordVO {

    @NotNull(message = "password can not be null.")
    @NotBlank(message = "password can not be blank.")
    @Size(min = 8)
    private String password;

    public void resetPassword(AccountEntity account, PasswordEncoder passwordEncoder) {
        account.setPassword(passwordEncoder.encode(this.password));
    }
}
