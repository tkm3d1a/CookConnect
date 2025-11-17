package com.tkforgeworks.cookconnect.recipeservice.controller;

import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeCreateDetailedRequestDto;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeCreateSimpleRequestDto;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeDto;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeSummaryDto;
import com.tkforgeworks.cookconnect.recipeservice.service.RecipeService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;

    //GET
    @GetMapping("/")
    @RateLimiter(name = "getAll")
    @Cacheable(value = "recipePages", key = "#page + '-' + #size")
    public ResponseEntity<Page<RecipeSummaryDto>> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort
    ) {
        Sort.Direction direction = sort[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<RecipeSummaryDto> recipes = recipeService.getAllRecipesSummary(pageable);
        return ResponseEntity.ok(recipes);
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
