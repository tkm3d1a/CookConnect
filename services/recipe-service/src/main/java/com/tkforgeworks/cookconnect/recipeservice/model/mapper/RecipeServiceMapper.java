package com.tkforgeworks.cookconnect.recipeservice.model.mapper;

import com.tkforgeworks.cookconnect.recipeservice.model.*;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.*;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecipeServiceMapper {
    RecipeDto toRecipeDto(Recipe recipe);
    IngredientListDto toIngredientListDto(IngredientList ingredientList);
    IngredientListItemDto toIngredientListItemDto(IngredientListItem ingredientListItem);
    IngredientDto toIngredientDto(Ingredient ingredient);
    InstructionListDto toInstructionListDto(InstructionList instructionList);
    InstructionListItemDto toInstructionListItemDto(InstructionListItem instructionListItem);
    InstructionDto toInstructionDto(Instruction instruction);
    TagListDto toTagListDto(TagList tagList);
    TagListItemDto toTagListItemDto(TagListItem tagListItem);
    TagDto toTagDto(Tag tag);

    Recipe toRecipe(RecipeDto recipeDto);
    IngredientList toIngredientList(IngredientListDto ingredientListDto);
    IngredientListItem toIngredientListItemDto(IngredientListItemDto ingredientListItemDto);
    Ingredient toIngredient(IngredientDto ingredientDto);
    InstructionList toInstructionList(InstructionListDto instructionListDto);
    InstructionListItem toInstructionListItem(InstructionListItemDto instructionListItemDto);
    Instruction toInstruction(InstructionDto instructionDto);
    TagList toTagList(TagListDto tagListDto);
    TagListItem toTagListItem(TagListItemDto tagListItemDto);
    Tag toTag(TagDto tagDto);

    List<RecipeDto> toRecipeDtos(List<Recipe> recipes);

    RecipeSummaryDto toRecipeSummaryDto(Recipe recipe);
    Recipe toRecipeFromCreate(RecipeCreateDto recipeCreateDto);
}
