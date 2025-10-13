package com.tkforgeworks.cookconnect.recipeservice.controller;

import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeCreateDetailedDto;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeCreateSimpleDto;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeDto;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeSummaryDto;
import com.tkforgeworks.cookconnect.recipeservice.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;

    //GET
    @GetMapping
    public ResponseEntity<List<RecipeSummaryDto>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipesSummary());
    }
    //POST
    @PostMapping
    public ResponseEntity<RecipeDto> createSimpleRecipe(@RequestBody RecipeCreateSimpleDto recipeCreateSimpleDto) {
        RecipeDto createdRecipe = recipeService.createSimpleRecipe(recipeCreateSimpleDto);
        URI location = URI.create(String.format("/recipes/%s", createdRecipe.id()));
        return ResponseEntity.created(location).body(createdRecipe);
    }
    @PostMapping("/detailed")
    public ResponseEntity<RecipeDto> createDetailedRecipe(@RequestBody RecipeCreateDetailedDto recipeCreateDetailedDto) {
        RecipeDto createdRecipe = recipeService.createdDetailedRecipe(recipeCreateDetailedDto);
        URI location = URI.create(String.format("/recipes/%s", createdRecipe.id()));
        return ResponseEntity.created(location).body(createdRecipe);
    }
    //PUT
    //DELETE
}
