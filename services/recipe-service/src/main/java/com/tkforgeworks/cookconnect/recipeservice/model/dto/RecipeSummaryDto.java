package com.tkforgeworks.cookconnect.recipeservice.model.dto;

import java.io.Serializable;

public record RecipeSummaryDto(Long id,
                               String title,
                               String createdByUsername)
        implements Serializable {}
