package com.tkforgeworks.cookconnect.recipeservice.message.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserChangeEvent {
    public enum ChangeType{
        ACCOUNT_CREATED,
        ACCOUNT_UPDATED,
        ACCOUNT_CLOSED,
        ACCOUNT_DELETED,
        PRIVACY_STATUS,
        SOCIAL_STATUS
    }

    private String userId;
    private ChangeType changeType;
    private LocalDateTime timestamp;
    private Map<String, Object> changeDetails;
}
