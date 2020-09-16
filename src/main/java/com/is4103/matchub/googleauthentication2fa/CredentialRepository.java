/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.googleauthentication2fa;

import com.warrenstrange.googleauth.ICredentialRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 *
 * @author ngjin
 */
@Component
public class CredentialRepository implements ICredentialRepository {

    private final Map<String, UserTOTP> usersKeys = new HashMap<String, UserTOTP>();

    @Override
    public String getSecretKey(String email) {
        return usersKeys.get(email).getSecretKey();
    }

    @Override
    public void saveUserCredentials(String email, String secretKey, int validationCode, List<Integer> scratchCodes) {
        usersKeys.put(email, new UserTOTP(email, secretKey, validationCode, scratchCodes));
    }

    public UserTOTP getUser(String email) {
        return usersKeys.get(email);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class UserTOTP {

        private String email;
        private String secretKey;
        private int validationCode;
        private List<Integer> scratchCodes;
    }
}
