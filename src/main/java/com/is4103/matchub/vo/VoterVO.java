/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author tjle2
 */
@Data
public class VoterVO {

    @NotNull(message = "First Name can not be null")
    @NotBlank(message = "First Name can not be empty")

    private String firstName;

    @NotNull(message = "Last Name can not be null")
    @NotBlank(message = "Last Name can not be empty")
    private String lastName;

    @NotNull(message = "Email can not be null")
    @NotBlank(message = "Email can not be empty")
    private String email;
}
