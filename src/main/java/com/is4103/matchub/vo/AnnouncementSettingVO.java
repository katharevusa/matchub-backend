/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.enumeration.AnnouncementTypeEnum;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author longluqian
 */
@Data
public class AnnouncementSettingVO {
    
    @NotNull
    Map<AnnouncementTypeEnum,Boolean> newSetting;
    
    @NotNull
    Long userId;
}
