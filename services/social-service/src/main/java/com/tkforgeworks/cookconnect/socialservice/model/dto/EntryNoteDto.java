package com.tkforgeworks.cookconnect.socialservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.tkforgeworks.cookconnect.socialservice.model.EntryNote}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EntryNoteDto(Long id,
                           String note,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt)
        implements Serializable {}