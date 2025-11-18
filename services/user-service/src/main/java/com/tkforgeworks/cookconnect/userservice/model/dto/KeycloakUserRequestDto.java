package com.tkforgeworks.cookconnect.userservice.model.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public record KeycloakUserRequestDto(String username,
                                     String email,
                                     String firstName,
                                     String lastName,
                                     boolean enabled,
                                     boolean emailVerified,
                                     List<Map<String, Object>> credentials)
        implements Serializable {}
