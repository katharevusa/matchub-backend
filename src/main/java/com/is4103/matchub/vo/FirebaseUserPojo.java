package com.is4103.matchub.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author tjle2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FirebaseUserPojo {
    
    private String uid;
    private String name;
    private String email;
    private String mobilePushToken;
    private String webPushToken;
}
