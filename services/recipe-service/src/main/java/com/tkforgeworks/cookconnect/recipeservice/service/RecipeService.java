package com.tkforgeworks.cookconnect.recipeservice.service;

import com.tkforgeworks.cookconnect.recipeservice.model.Recipe;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeCreateDto;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeDto;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeSummaryDto;
import com.tkforgeworks.cookconnect.recipeservice.model.mapper.RecipeServiceMapper;
import com.tkforgeworks.cookconnect.recipeservice.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final RecipeNoteService recipeNoteService;
    private final TagService tagService;
    private final IngredientService ingredientService;
    private final InstructionService instructionService;
    private final RecipeServiceMapper mapper;


    //GET
    public List<RecipeSummaryDto> getAllRecipesSummary() {
        return recipeRepository.findAll().stream().map(mapper::toRecipeSummaryDto).collect(Collectors.toList());
    }

    //POST
    public RecipeDto createRecipe(RecipeCreateDto recipeCreateDto) {
        Recipe toCreate = mapper.toRecipeFromCreate(recipeCreateDto);
        toCreate.setIngredientList(ingredientService.createBlankList());
        toCreate.setInstructionList(instructionService.createBlankList());
        toCreate.setTagList(tagService.createBlankList());
        Recipe savedRecipe = recipeRepository.save(toCreate);
        return mapper.toRecipeDto(savedRecipe);
    }
    //PUT
    //DELETE
    //PRIVATE - UTILITY
}
