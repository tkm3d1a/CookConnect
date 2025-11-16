package com.tkforgeworks.cookconnect.userservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record KeycloakTokenResponseDto(@JsonProperty("access_token") String accessToken,
                                       @JsonProperty("expires_in") Integer expiresIn,
                                       @JsonProperty("refresh_expires_in") Integer refreshExpiresIn,
                                       @JsonProperty("token_type") String tokenType,
                                       @JsonProperty("not-before-policy") Integer notBeforePolicy,
                                       String scope)
        implements Serializable {}
