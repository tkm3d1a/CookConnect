package com.tkforgeworks.cookconnect.recipeservice.controller;

import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeCreateDetailedRequestDto;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeCreateSimpleRequestDto;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeDto;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeSummaryDto;
import com.tkforgeworks.cookconnect.recipeservice.service.RecipeService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;

    //GET
    @GetMapping
    @RateLimiter(name = "getAll")
    public ResponseEntity<List<RecipeSummaryDto>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipesSummary());
    }
    //POST
    @PostMapping("/simple")
    public ResponseEntity<RecipeDto> createSimpleRecipe(@RequestBody RecipeCreateSimpleRequestDto recipeCreateSimpleRequestDto) {
        RecipeDto createdRecipe = recipeService.createSimpleRecipe(recipeCreateSimpleRequestDto);
        URI location = URI.create(String.format("/recipes/%s", createdRecipe.id()));
        return ResponseEntity.created(location).body(createdRecipe);
    }
    @PostMapping("/detailed")
    public ResponseEntity<RecipeDto> createDetailedRecipe(@RequestBody RecipeCreateDetailedRequestDto recipeCreateDetailedRequestDto) {
        RecipeDto createdRecipe = recipeService.createdDetailedRecipe(recipeCreateDetailedRequestDto);
        URI location = URI.create(String.format("/recipes/%s", createdRecipe.id()));
        return ResponseEntity.created(location).body(createdRecipe);
    }
    //PUT
    //DELETE
}
