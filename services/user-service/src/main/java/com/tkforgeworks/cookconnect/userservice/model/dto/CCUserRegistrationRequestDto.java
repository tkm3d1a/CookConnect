package com.tkforgeworks.cookconnect.userservice.model.dto;

import java.io.Serializable;

public record CCUserRegistrationRequestDto(String username,
                                           String password,
                                           String email,
                                           String firstName,
                                           String lastName
                                    )
        implements Serializable {}
