package com.tkforgeworks.cookconnect.socialservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tkforgeworks.cookconnect.socialservice.model.SocialInteraction;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link SocialInteraction}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SocialInteractionDto(String forUserId, //required
                                   Set<Long> followingIds,
                                   Set<Long> followerIds,
                                   Set<Long> bookmarkedRecipeIds,
                                   Set<CookbookDto> cookbooks)
        implements Serializable {}