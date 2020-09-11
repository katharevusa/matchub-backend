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
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author ngjin
 */
@Data
public class ChangePasswordVO {
    
    @NotBlank(message = "Password cannot be blank.")
    @NotNull(message = "Password cannot be null.")
    @Size(min = 8, message = "Password entered must have minimum length of 8.")
    private String password;
    
    public void changePassword(AccountEntity account, PasswordEncoder passwordEncoder) {
        if (!this.password.isEmpty()) {
            account.setPassword(passwordEncoder.encode(this.password));
        }
    }
}
