package com.tkforgeworks.recipeservice.service;

import com.tkforgeworks.recipeservice.repository.InstructionListItemRepository;
import com.tkforgeworks.recipeservice.repository.InstructionListRepository;
import com.tkforgeworks.recipeservice.repository.InstructionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstructionService {
    private final InstructionRepository instructionRepository;
    private final InstructionListRepository instructionListRepository;
    private final InstructionListItemRepository instructionListItemRepository;
}
