package com.tkforgeworks.cookconnect.recipeservice.model.dto;

import java.io.Serializable;

public record RecipeCreateSimpleRequestDto(String title,
                                           String description,
                                           String createdBy)
        implements Serializable {}
