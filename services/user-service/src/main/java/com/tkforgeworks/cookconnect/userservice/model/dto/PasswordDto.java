package com.tkforgeworks.cookconnect.userservice.model.dto;

import java.io.Serializable;

public record PasswordDto(String username,
                          String password)
        implements Serializable { }
