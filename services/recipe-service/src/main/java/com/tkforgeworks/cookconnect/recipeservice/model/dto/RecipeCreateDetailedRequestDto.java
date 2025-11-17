package com.tkforgeworks.cookconnect.recipeservice.model.dto;

import com.tkforgeworks.cookconnect.recipeservice.model.enums.SkillLevel;

import java.io.Serializable;

public record RecipeCreateDetailedRequestDto(String title,
                                             String description,
                                             String createdBy,
                                             SkillLevel skillLevel,
                                             IngredientListDto ingredientList,
                                             InstructionListDto instructionList,
                                             TagListDto tagList)
        implements Serializable {}
