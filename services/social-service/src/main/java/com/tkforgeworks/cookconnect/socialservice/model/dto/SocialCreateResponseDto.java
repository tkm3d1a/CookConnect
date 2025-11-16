package com.tkforgeworks.cookconnect.socialservice.model.dto;

import java.io.Serializable;

public record SocialCreateResponseDto(Long id,
                                      boolean hasSocialInteraction,
                                      boolean privateAccount)
        implements Serializable {}
