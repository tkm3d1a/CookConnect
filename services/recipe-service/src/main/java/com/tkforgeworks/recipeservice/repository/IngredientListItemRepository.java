package com.tkforgeworks.recipeservice.repository;

import com.tkforgeworks.recipeservice.model.IngredientListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientListItemRepository extends JpaRepository<IngredientListItem, Long> {
}
