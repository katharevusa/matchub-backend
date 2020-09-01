package com.is4103.matchub.vo;

import com.is4103.matchub.entity.AccountEntity;
import java.time.LocalDateTime;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class UserVO {

    private Long accountId;
    private UUID uuid;
    private String email;
    private Set<String> roles = new HashSet<>();
    private Boolean accountLocked;
    private Boolean accountExpired;
    private Boolean disabled;
    private Boolean isVerified;
    private LocalDateTime joinDate;

    static public UserVO of(AccountEntity account) {
        UserVO vo = new UserVO();
        vo.accountId = account.getAccountId();
        vo.uuid = account.getUuid();
        vo.email = account.getEmail();
        vo.roles = account.getRoles();
        vo.accountLocked = account.getAccountLocked();
        vo.accountExpired = account.getAccountExpired();
        vo.disabled = account.getDisabled();
        vo.isVerified = account.getIsVerified();
        vo.joinDate = account.getJoinDate();
        return vo;
    }
}
