package com.tkforgeworks.cookconnect.userservice.model.dto;

import com.tkforgeworks.cookconnect.userservice.model.enums.SkillLevel;

import java.io.Serializable;

public record UpdateCCUserDTO(Long id,
                              String username,
                              String email,
                              boolean hasSocialInteraction,
                              boolean privateAccount,
                              boolean closedAccount,
                              SkillLevel skillLevel)
        implements Serializable {}
