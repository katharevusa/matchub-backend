/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.ProfileEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author longluqian
 */
@Data
public class ChannelDetailsVO {

    public List<Long> adminIds = new ArrayList<>();
    public List<Long> memberIds = new ArrayList<>();
    public Long creatorId;

}
