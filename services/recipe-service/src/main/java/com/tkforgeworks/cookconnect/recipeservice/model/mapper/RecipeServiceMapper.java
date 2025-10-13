package com.tkforgeworks.cookconnect.recipeservice.model.mapper;

import com.tkforgeworks.cookconnect.recipeservice.model.*;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecipeServiceMapper {
    //To DTO's
    RecipeDto toRecipeDto(Recipe recipe);
    @Mapping(source = "ingredients", target = "listItems")
    IngredientListDto toIngredientListDto(IngredientList ingredientList);
    @Mapping(source = "quantity", target = "qty")
    IngredientListItemDto toIngredientListItemDto(IngredientListItem ingredientListItem);
    IngredientDto toIngredientDto(Ingredient ingredient);
    @Mapping(source = "instructions", target = "listItems")
    InstructionListDto toInstructionListDto(InstructionList instructionList);
    InstructionListItemDto toInstructionListItemDto(InstructionListItem instructionListItem);
    InstructionDto toInstructionDto(Instruction instruction);
    @Mapping(source = "tags", target = "listItems")
    TagListDto toTagListDto(TagList tagList);
    TagListItemDto toTagListItemDto(TagListItem tagListItem);
    TagDto toTagDto(Tag tag);

    //From DTO's
    Recipe toRecipe(RecipeDto recipeDto);
    @Mapping(source = "listItems", target = "ingredients")
    IngredientList toIngredientList(IngredientListDto ingredientListDto);
    @Mapping(source = "qty", target = "quantity")
    IngredientListItem toIngredientListItem(IngredientListItemDto ingredientListItemDto);
    Ingredient toIngredient(IngredientDto ingredientDto);
    @Mapping(source = "listItems", target = "instructions")
    InstructionList toInstructionList(InstructionListDto instructionListDto);
    InstructionListItem toInstructionListItem(InstructionListItemDto instructionListItemDto);
    Instruction toInstruction(InstructionDto instructionDto);
    @Mapping(source = "listItems", target = "tags")
    TagList toTagList(TagListDto tagListDto);
    TagListItem toTagListItem(TagListItemDto tagListItemDto);
    Tag toTag(TagDto tagDto);

    /* Need to refactor section */
    List<RecipeDto> toRecipeDtos(List<Recipe> recipes);

    RecipeSummaryDto toRecipeSummaryDto(Recipe recipe);
    Recipe toRecipeFromCreateSimple(RecipeCreateSimpleDto recipeCreateSimpleDto);
    Recipe toRecipeFromCreateDetailed(RecipeCreateDetailedDto recipeCreateDetailedDto);
}
