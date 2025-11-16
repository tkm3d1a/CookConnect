package com.tkforgeworks.cookconnect.userservice.model.dto;

import com.tkforgeworks.cookconnect.userservice.model.enums.SkillLevel;

import java.io.Serializable;
import java.time.LocalDateTime;

public record CCUserDto(String id,
                        String username,
                        String email,
                        boolean hasSocialInteraction,
                        boolean privateAccount,
                        boolean closedAccount,
                        LocalDateTime createdAt,
                        LocalDateTime updatedAt,
                        SkillLevel skillLevel,
                        ProfileInfoDto profileInfo)
        implements Serializable {}