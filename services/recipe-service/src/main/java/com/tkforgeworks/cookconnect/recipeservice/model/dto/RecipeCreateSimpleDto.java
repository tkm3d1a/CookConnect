package com.tkforgeworks.cookconnect.recipeservice.model.dto;

import java.io.Serializable;

public record RecipeCreateSimpleDto(String title,
                                    String description,
                                    Long createdBy)
        implements Serializable {}
