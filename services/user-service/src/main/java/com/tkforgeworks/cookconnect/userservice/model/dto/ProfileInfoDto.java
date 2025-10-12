package com.tkforgeworks.cookconnect.userservice.model.dto;

import com.tkforgeworks.cookconnect.userservice.model.enums.Gender;
import com.tkforgeworks.cookconnect.userservice.model.enums.Pronouns;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

public record ProfileInfoDto(Long id,
                             String firstName,
                             String lastName,
                             Date birthDate,
                             int age,
                             Gender gender,
                             Pronouns pronouns,
                             Set<AddressDto> addresses,
                             LocalDateTime createdAt,
                             LocalDateTime updatedAt)
        implements Serializable {}