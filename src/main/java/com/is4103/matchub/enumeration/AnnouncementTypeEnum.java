/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.enumeration;

/**
 *
 * @author longluqian
 */
public enum AnnouncementTypeEnum {
    
// project announcments
PROJECT_INTERNAL_ANNOUNCEMENT,
PROJECT_PUBLIC_ANNOUNCEMENT,
// task related
TASK_ASSIGNED,
TASK_LEADER_APPOINTMENT,
NEW_PROFILE_FOLLOWER,
TASK_UPDATE,

//post related
SHARE_POST,
NEW_POST_COMMENT,
NEW_POST,
NEW_POST_LIKE,

//resource request related
REQUEST_FOR_RESOURCE,
RESOURCE_REQUEST_ACCEPTED,
RESOURCE_REQUEST_REJECTED,
DONATE_TO_PROJECT,
RESOURCE_DONATION_ACCEPTED,
RESOURCE_DONATION_REJECTED,
DELETE_RESOURCE_REQUEST,

//join project related
JOIN_PROJ_REQUEST,
LEAVE_PROJECT,
ACCEPT_JOIN_REQUEST,
REJECT_JOIN_REQUEST,
REMOVE_MEMBER,

NEW_PROJECT_FOLLOWER,
FUND_PLEGDE,

// review related
RECEIVING_NEW_REVIEW,

PROJECT_COMPLETED

}
