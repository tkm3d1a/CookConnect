package com.tkforgeworks.cookconnect.socialservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tkforgeworks.cookconnect.socialservice.model.CookbookEntry;

import java.io.Serializable;

/**
 * DTO for {@link CookbookEntry}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CookbookEntryDto(Long id,
                               Long recipeId,
                               EntryNoteDto entryNote)
        implements Serializable {}