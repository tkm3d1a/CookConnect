package com.tkforgeworks.recipeservice.service;

import com.tkforgeworks.recipeservice.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final RecipeNoteService recipeNoteService;
    private final TagService tagService;
    private final IngredientService ingredientService;
    private final InstructionService instructionService;
}
