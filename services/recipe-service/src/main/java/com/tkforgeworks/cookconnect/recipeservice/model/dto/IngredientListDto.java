package com.tkforgeworks.cookconnect.recipeservice.model.dto;

import java.io.Serializable;
import java.util.Set;

public record IngredientListDto(Set<IngredientListItemDto> listItems)
        implements Serializable {}
