package com.tkforgeworks.cookconnect.recipeservice.model.dto;

import java.io.Serializable;
import java.util.Set;

public record InstructionListDto(Set<InstructionListItemDto> listItems)
        implements Serializable {}
