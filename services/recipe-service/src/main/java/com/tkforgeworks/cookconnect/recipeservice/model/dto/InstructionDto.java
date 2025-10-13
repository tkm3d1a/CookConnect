package com.tkforgeworks.cookconnect.recipeservice.model.dto;

import java.io.Serializable;

public record InstructionDto(String text,
                             String note)
        implements Serializable {}
