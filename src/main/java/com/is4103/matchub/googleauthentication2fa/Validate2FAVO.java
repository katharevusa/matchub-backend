/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.googleauthentication2fa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author ngjin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class Validate2FAVO {

    private String email;
    private Integer code;

}
