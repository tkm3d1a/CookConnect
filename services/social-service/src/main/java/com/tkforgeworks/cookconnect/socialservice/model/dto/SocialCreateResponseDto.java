package com.tkforgeworks.cookconnect.socialservice.model.dto;

import java.io.Serializable;

public record SocialCreateResponseDto(String id,
                                      boolean hasSocialInteraction,
                                      boolean privateAccount)
        implements Serializable {}
