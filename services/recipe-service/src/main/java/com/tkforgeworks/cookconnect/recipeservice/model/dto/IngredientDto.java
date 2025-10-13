package com.tkforgeworks.cookconnect.recipeservice.model.dto;

import java.io.Serializable;

public record IngredientDto(String name,
                            String description,
                            String link)
        implements Serializable {}
