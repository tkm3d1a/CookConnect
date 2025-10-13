package com.tkforgeworks.cookconnect.recipeservice.model.dto;

import java.io.Serializable;
import java.util.Set;

public record TagListDto(Set<TagListItemDto> listItems)
        implements Serializable {}
