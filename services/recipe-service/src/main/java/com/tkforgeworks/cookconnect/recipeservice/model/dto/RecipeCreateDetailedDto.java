package com.tkforgeworks.cookconnect.recipeservice.model.dto;

import com.tkforgeworks.cookconnect.recipeservice.model.enums.SkillLevel;

import java.io.Serializable;

public record RecipeCreateDetailedDto(String title,
                                      String description,
                                      Long createdBy,
                                      SkillLevel skillLevel,
                                      IngredientListDto ingredientList,
                                      InstructionListDto instructionList,
                                      TagListDto tagList)
        implements Serializable {}
