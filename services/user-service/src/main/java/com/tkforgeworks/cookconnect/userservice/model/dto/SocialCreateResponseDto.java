package com.tkforgeworks.cookconnect.userservice.model.dto;

import java.io.Serializable;

public record SocialCreateResponseDto(String id,
                                      boolean hasSocialInteraction,
                                      boolean privateAccount)
        implements Serializable {}
