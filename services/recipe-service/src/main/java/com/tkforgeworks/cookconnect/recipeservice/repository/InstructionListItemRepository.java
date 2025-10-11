package com.tkforgeworks.cookconnect.recipeservice.repository;

import com.tkforgeworks.cookconnect.recipeservice.model.InstructionListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructionListItemRepository extends JpaRepository<InstructionListItem, Long> {
}
