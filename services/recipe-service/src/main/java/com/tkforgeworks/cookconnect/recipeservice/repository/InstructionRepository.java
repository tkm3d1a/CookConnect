package com.tkforgeworks.cookconnect.recipeservice.repository;

import com.tkforgeworks.cookconnect.recipeservice.model.Instruction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructionRepository extends JpaRepository<Instruction, Long> {
}
