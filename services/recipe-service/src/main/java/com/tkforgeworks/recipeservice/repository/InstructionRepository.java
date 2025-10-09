package com.tkforgeworks.recipeservice.repository;

import com.tkforgeworks.recipeservice.model.Instruction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructionRepository extends JpaRepository<Instruction, Long> {
}
