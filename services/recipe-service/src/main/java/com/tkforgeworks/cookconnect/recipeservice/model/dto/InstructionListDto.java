package com.tkforgeworks.cookconnect.recipeservice.model.dto;

import java.io.Serializable;
import java.util.List;

public record InstructionListDto(List<InstructionListItemDto> listItem)
        implements Serializable {}
