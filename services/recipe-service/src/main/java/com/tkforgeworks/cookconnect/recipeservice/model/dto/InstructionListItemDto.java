package com.tkforgeworks.cookconnect.recipeservice.model.dto;

import java.io.Serializable;

public record InstructionListItemDto(int stepNumber,
                                     InstructionDto instruction)
        implements Serializable {}
