package com.tkforgeworks.cookconnect.recipeservice.model.dto;

import com.tkforgeworks.cookconnect.recipeservice.model.enums.TagCategory;

import java.io.Serializable;

public record TagDto(String name,
                     String description,
                     TagCategory tagCategory)
        implements Serializable {}
