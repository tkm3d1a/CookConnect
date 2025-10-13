package com.tkforgeworks.cookconnect.recipeservice.model.dto;

import com.tkforgeworks.cookconnect.recipeservice.model.RecipeNote;
import com.tkforgeworks.cookconnect.recipeservice.model.enums.SkillLevel;

import java.io.Serializable;
import java.util.List;

public record RecipeDto(Long id,
                        String title,
                        String description,
                        Long createdBy,
                        SkillLevel skillLevel,
                        IngredientListDto ingredientList,
                        InstructionListDto instructionList,
                        TagListDto tagList,
                        List<RecipeNote> notes)
        implements Serializable {}
