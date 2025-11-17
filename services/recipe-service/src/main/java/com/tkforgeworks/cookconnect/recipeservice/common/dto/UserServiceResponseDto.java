package com.tkforgeworks.cookconnect.recipeservice.common.dto;

import java.io.Serializable;

public record UserServiceResponseDto(String id,
                                     String username,
                                     boolean hasSocialInteraction,
                                     boolean privateAccount)
        implements Serializable {}
