package com.tkforgeworks.cookconnect.recipeservice.model.dto;

import java.io.Serializable;
import java.util.List;

public record TagListDto(List<TagListItemDto> listItem)
        implements Serializable {}
