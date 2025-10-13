package com.tkforgeworks.cookconnect.recipeservice.model.dto;

import com.tkforgeworks.cookconnect.recipeservice.model.enums.MeasurementValue;

import java.io.Serializable;

public record IngredientListItemDto(int qty,
                                    MeasurementValue measurementValue,
                                    IngredientDto ingredient)
        implements Serializable {}
