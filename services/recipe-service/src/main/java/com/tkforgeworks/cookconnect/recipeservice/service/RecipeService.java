package com.tkforgeworks.cookconnect.recipeservice.service;

import com.tkforgeworks.cookconnect.recipeservice.model.Recipe;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeCreateDetailedDto;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeCreateSimpleDto;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeDto;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeSummaryDto;
import com.tkforgeworks.cookconnect.recipeservice.model.mapper.RecipeServiceMapper;
import com.tkforgeworks.cookconnect.recipeservice.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
    public RecipeDto createSimpleRecipe(RecipeCreateSimpleDto recipeCreateSimpleDto) {
        Recipe toCreate = mapper.toRecipeFromCreateSimple(recipeCreateSimpleDto);
        toCreate.setIngredientList(ingredientService.createBlankList());
        toCreate.setInstructionList(instructionService.createBlankList());
        toCreate.setTagList(tagService.createBlankList());

        return mapper.toRecipeDto(recipeRepository.save(toCreate));
    }

    public RecipeDto createdDetailedRecipe(RecipeCreateDetailedDto recipeCreateDetailedDto) {
        log.info("CreateDetailedRecipe:\n\t{}", recipeCreateDetailedDto);
        Recipe toCreate = mapper.toRecipeFromCreateDetailed(recipeCreateDetailedDto);

        if(recipeCreateDetailedDto.ingredientList() == null){
            toCreate.setIngredientList(ingredientService.createBlankList());
        } else {
            toCreate.setIngredientList(ingredientService.createIngredientList(recipeCreateDetailedDto.ingredientList()));
        }
        if(recipeCreateDetailedDto.instructionList() == null){
            toCreate.setInstructionList(instructionService.createBlankList());
        } else {
            toCreate.setInstructionList(instructionService.createInstructionList(recipeCreateDetailedDto.instructionList()));
        }
        if(recipeCreateDetailedDto.tagList() == null){
            toCreate.setTagList(tagService.createBlankList());
        } else {
            toCreate.setTagList(tagService.createTagList(recipeCreateDetailedDto.tagList()));
        }

        return mapper.toRecipeDto(recipeRepository.save(toCreate));
    }
    //PUT
    //DELETE
    //PRIVATE - UTILITY
}
