package com.tkforgeworks.cookconnect.userservice.model.dto;

import com.tkforgeworks.cookconnect.userservice.model.enums.SkillLevel;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.tkforgeworks.cookconnect.userservice.model.CCUser}
 */
public record CCUserDto(Long id, String username, String email, boolean hasSocialInteraction, boolean isPrivate,
                        boolean isClosed, LocalDateTime createdAt, LocalDateTime updatedAt, SkillLevel skillLevel,
                        ProfileInfoDto profileInfo) implements Serializable {
}