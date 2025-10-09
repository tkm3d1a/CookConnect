package com.tkforgeworks.recipeservice.repository;

import com.tkforgeworks.recipeservice.model.InstructionList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructionListRepository extends JpaRepository<InstructionList, Long> {
}
