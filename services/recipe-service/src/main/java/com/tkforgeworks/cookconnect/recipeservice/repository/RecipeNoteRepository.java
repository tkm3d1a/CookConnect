package com.tkforgeworks.cookconnect.recipeservice.repository;

import com.tkforgeworks.cookconnect.recipeservice.model.RecipeNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeNoteRepository extends JpaRepository<RecipeNote, Long> {
}
