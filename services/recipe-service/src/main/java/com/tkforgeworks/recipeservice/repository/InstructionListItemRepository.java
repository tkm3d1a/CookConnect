package com.tkforgeworks.recipeservice.repository;

import com.tkforgeworks.recipeservice.model.InstructionListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructionListItemRepository extends JpaRepository<InstructionListItem, Long> {
}
