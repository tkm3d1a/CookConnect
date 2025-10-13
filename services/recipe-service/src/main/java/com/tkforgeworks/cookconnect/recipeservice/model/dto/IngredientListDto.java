package com.tkforgeworks.cookconnect.recipeservice.model.dto;

import java.io.Serializable;
import java.util.List;

public record IngredientListDto(List<IngredientListItemDto> listItem)
        implements Serializable {}
