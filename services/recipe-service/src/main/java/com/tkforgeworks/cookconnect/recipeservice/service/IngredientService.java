package com.tkforgeworks.cookconnect.recipeservice.service;

import com.tkforgeworks.cookconnect.recipeservice.model.IngredientList;
import com.tkforgeworks.cookconnect.recipeservice.repository.IngredientListItemRepository;
import com.tkforgeworks.cookconnect.recipeservice.repository.IngredientListRepository;
import com.tkforgeworks.cookconnect.recipeservice.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final IngredientListRepository ingredientListRepository;
    private final IngredientListItemRepository ingredientListItemRepository;

    //INTERNAL
    protected IngredientList createBlankList() {
        IngredientList ingredientList = new IngredientList();
        return ingredientListRepository.save(ingredientList);
    }
}
