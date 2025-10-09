package com.tkforgeworks.recipeservice.repository;

import com.tkforgeworks.recipeservice.model.RecipeNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeNoteRepository extends JpaRepository<RecipeNote, Long> {
}
