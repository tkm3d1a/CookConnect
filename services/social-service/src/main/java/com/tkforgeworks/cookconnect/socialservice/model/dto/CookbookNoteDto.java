package com.tkforgeworks.cookconnect.socialservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tkforgeworks.cookconnect.socialservice.model.CookbookNote;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link CookbookNote}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CookbookNoteDto(Long id,
                              String note,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt)
        implements Serializable {}