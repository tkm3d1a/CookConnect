package com.tkforgeworks.cookconnect.socialservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * DTO for {@link com.tkforgeworks.cookconnect.socialservice.model.EntryNote}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EntryNoteDto(String note)
        implements Serializable {}