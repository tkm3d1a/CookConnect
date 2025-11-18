package com.tkforgeworks.cookconnect.recipeservice.service;

import com.tkforgeworks.cookconnect.recipeservice.model.Instruction;
import com.tkforgeworks.cookconnect.recipeservice.model.InstructionList;
import com.tkforgeworks.cookconnect.recipeservice.model.InstructionListItem;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.InstructionListDto;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.InstructionListItemDto;
import com.tkforgeworks.cookconnect.recipeservice.model.mapper.RecipeServiceMapper;
import com.tkforgeworks.cookconnect.recipeservice.repository.InstructionListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstructionService {
//    private final InstructionRepository instructionRepository;
    private final InstructionListRepository instructionListRepository;
//    private final InstructionListItemRepository instructionListItemRepository;
    private final RecipeServiceMapper mapper;
    //INTERNAL
    protected InstructionList createBlankList() {
        InstructionList instructionList = new InstructionList();
        return instructionListRepository.save(instructionList);
    }

    public InstructionList createInstructionList(InstructionListDto instructionListDto) {
        InstructionList instructionList = new InstructionList();

        for(InstructionListItemDto itemDto: instructionListDto.listItems()){
            Instruction instruction = mapper.toInstruction(itemDto.instruction());
            InstructionListItem listItem = new InstructionListItem();
            listItem.setInstruction(instruction);
            listItem.setStepNumber(itemDto.stepNumber());
            instructionList.addListItem(listItem);
        }

        return instructionListRepository.save(instructionList);
    }
}
