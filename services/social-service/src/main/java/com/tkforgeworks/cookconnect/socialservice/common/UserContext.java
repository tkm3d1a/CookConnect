package com.tkforgeworks.cookconnect.socialservice.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class UserContext {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHENTICATION_HEADER = "X-Authentication-Token";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String SOCIAL_ID_HEADER = "X-Social-Id";
    public static final String USERNAME_HEADER = "X-Username";

    private String correlationId = "";
    private String authToken = "";
    private String userId = "";
    private String socialId = "";
    private String username = "";
}
