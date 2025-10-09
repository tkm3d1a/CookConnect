package com.tkforgeworks.recipeservice.service;

import com.tkforgeworks.recipeservice.repository.IngredientListItemRepository;
import com.tkforgeworks.recipeservice.repository.IngredientListRepository;
import com.tkforgeworks.recipeservice.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final IngredientListRepository ingredientListRepository;
    private final IngredientListItemRepository ingredientListItemRepository;
}
