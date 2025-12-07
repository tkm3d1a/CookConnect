package com.tkforgeworks.cookconnect.recipeservice.repository;

import com.tkforgeworks.cookconnect.recipeservice.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findRecipeByCreatedBy(String createdBy);
}