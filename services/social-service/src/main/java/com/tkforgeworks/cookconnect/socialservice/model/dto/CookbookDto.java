package com.tkforgeworks.cookconnect.socialservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link com.tkforgeworks.cookconnect.socialservice.model.Cookbook}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CookbookDto(Long id,
                          String name,
                          String description,
                          LocalDateTime createdAt,
                          LocalDateTime updatedAt,
                          CookbookNoteDto note,
                          Set<CookbookEntryDto> cookBookEntries)
        implements Serializable {}