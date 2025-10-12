package com.tkforgeworks.cookconnect.socialservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * DTO for {@link com.tkforgeworks.cookconnect.socialservice.model.Cookbook}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CookbookDto(Long id,
                          String name,
                          String description)
        implements Serializable {}