package com.tkforgeworks.cookconnect.recipeservice.service;

import com.tkforgeworks.cookconnect.recipeservice.model.InstructionList;
import com.tkforgeworks.cookconnect.recipeservice.repository.InstructionListItemRepository;
import com.tkforgeworks.cookconnect.recipeservice.repository.InstructionListRepository;
import com.tkforgeworks.cookconnect.recipeservice.repository.InstructionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstructionService {
    private final InstructionRepository instructionRepository;
    private final InstructionListRepository instructionListRepository;
    private final InstructionListItemRepository instructionListItemRepository;

    //INTERNAL
    protected InstructionList createBlankList() {
        InstructionList instructionList = new InstructionList();
        return instructionListRepository.save(instructionList);
    }
}
