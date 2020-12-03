package com.is4103.matchub.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author tjle2
 */
@Data
public class ClaimRequestVO {

    @NotNull(message = "Email can not be null")
    @NotBlank(message = "Email can not be empty")
    private String email;
    
    @NotNull(message = "Account Id can not be null")
    private Long accountId;
}
