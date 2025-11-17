package com.tkforgeworks.cookconnect.socialservice.common.dto;

import java.io.Serializable;

public record UserServiceResponseDto(String id,
                                     String username,
                                     boolean hasSocialInteraction,
                                     boolean privateAccount)
        implements Serializable {}
