package com.tkforgeworks.recipeservice.repository;

import com.tkforgeworks.recipeservice.model.IngredientList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientListRepository extends JpaRepository<IngredientList, Long> {
}
