package com.is4103.matchub.vo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author tjle2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationsToUsersVO {

    private List<String> uuids;
    private String type;
    private String title;
    private String body;
    private String image;
}
