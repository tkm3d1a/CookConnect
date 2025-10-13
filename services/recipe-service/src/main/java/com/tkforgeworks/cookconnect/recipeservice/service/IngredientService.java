package com.tkforgeworks.cookconnect.recipeservice.service;

import com.tkforgeworks.cookconnect.recipeservice.model.Ingredient;
import com.tkforgeworks.cookconnect.recipeservice.model.IngredientList;
import com.tkforgeworks.cookconnect.recipeservice.model.IngredientListItem;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.IngredientDto;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.IngredientListDto;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.IngredientListItemDto;
import com.tkforgeworks.cookconnect.recipeservice.model.mapper.RecipeServiceMapper;
import com.tkforgeworks.cookconnect.recipeservice.repository.IngredientListItemRepository;
import com.tkforgeworks.cookconnect.recipeservice.repository.IngredientListRepository;
import com.tkforgeworks.cookconnect.recipeservice.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final IngredientListRepository ingredientListRepository;
    private final IngredientListItemRepository ingredientListItemRepository;
    private final RecipeServiceMapper mapper;

    //INTERNAL
    protected IngredientList createBlankList() {
        IngredientList ingredientList = new IngredientList();
        return ingredientListRepository.save(ingredientList);
    }

    protected IngredientList createIngredientList(IngredientListDto ingredientListDto) {
        IngredientList ingredientList = new IngredientList();

        for(IngredientListItemDto itemDto: ingredientListDto.listItems()){
            Ingredient ingredient = findOrCreateIngredient(itemDto.ingredient());
            IngredientListItem ingredientListItem = new IngredientListItem();
            ingredientListItem.setIngredient(ingredient);
            ingredientListItem.setMeasurementValue(itemDto.measurementValue());
            ingredientListItem.setQuantity(itemDto.qty());

            ingredientList.addListItem(ingredientListItem);
        }

        return ingredientListRepository.save(ingredientList);
    }

    //PRIVATE
    private Ingredient findOrCreateIngredient(IngredientDto ingredientDto){
        return ingredientRepository.findByName(ingredientDto.name())
                .orElseGet(() -> ingredientRepository.save(mapper.toIngredient(ingredientDto)));
    }
}
