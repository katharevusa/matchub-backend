package com.is4103.matchub.vo;

import java.util.List;
import lombok.Data;

/**
 *
 * @author tjle2
 */
@Data
public class SendNotificationsToUsersVO {
    
    private List<String> uuids;
    private String type;
    private String chatId;
    private String title;
    private String body;
    private String image;
}
