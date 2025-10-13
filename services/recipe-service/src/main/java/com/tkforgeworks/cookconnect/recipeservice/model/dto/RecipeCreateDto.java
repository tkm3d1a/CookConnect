package com.tkforgeworks.cookconnect.recipeservice.model.dto;

import java.io.Serializable;

public record RecipeCreateDto(String title,
                              String description,
                              Long createdBy)
        implements Serializable {}
