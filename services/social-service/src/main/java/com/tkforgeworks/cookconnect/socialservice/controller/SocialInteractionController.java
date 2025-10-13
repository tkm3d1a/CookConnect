package com.tkforgeworks.cookconnect.socialservice.controller;

import com.tkforgeworks.cookconnect.socialservice.model.dto.SocialInteractionDto;
import com.tkforgeworks.cookconnect.socialservice.service.SocialInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/social")
@RequiredArgsConstructor
public class SocialInteractionController {
    private final SocialInteractionService socialInteractionService;

    //GET
    @GetMapping("/{socialId}")
    public ResponseEntity<SocialInteractionDto> getSocialInteraction(@PathVariable("socialId") Long socialId) {
        return ResponseEntity.ok(socialInteractionService.getSocialProfile(socialId));
    }
    @GetMapping("/{socialId}/followers")
    public ResponseEntity<List<Long>> getFollowers(@PathVariable("socialId") Long socialId) {
        return ResponseEntity.ok(socialInteractionService.getFollowers(socialId));
    }
    @GetMapping("/{socialId}/following")
    public ResponseEntity<List<Long>> getFollowing(@PathVariable("socialId") Long socialId) {
        return ResponseEntity.ok(socialInteractionService.getFollowing(socialId));
    }
    @GetMapping("/{socialId}/bookmarks")
    public ResponseEntity<List<Long>> getBookmarks(@PathVariable("socialId") Long socialId) {
        return ResponseEntity.ok(socialInteractionService.getBookmarks(socialId));
    }
    //POST
    @PostMapping
    public ResponseEntity<SocialInteractionDto> createSocialInteraction(@RequestBody SocialInteractionDto socialInteractionDto) {
        SocialInteractionDto createdSocial = socialInteractionService.createNewSocial(socialInteractionDto);
        URI location = URI.create("/social/" + createdSocial.forUserId());
        return ResponseEntity.created(location).body(createdSocial);
    }
    @PostMapping("/{socialId}/follow/{targetUserId}")
    public ResponseEntity<SocialInteractionDto> followTargetId(@PathVariable("socialId") Long socialId,
                                                               @PathVariable("targetUserId") Long targetUserId) {
        return ResponseEntity.accepted().body(socialInteractionService.followTargetUser(socialId, targetUserId));
    }
    @PostMapping("/{socialId}/bookmark/{targetRecipeId}")
    public ResponseEntity<SocialInteractionDto> bookmarkTargetRecipe(@PathVariable("socialId") Long socialId,
                                                                     @PathVariable("targetRecipeId") Long targetRecipeId) {
        return ResponseEntity.accepted().body(socialInteractionService.bookmarkTargetRecipe(socialId, targetRecipeId));
    }
    //PUT
    //DELETE
    @DeleteMapping("/{socialId}/follow/{targetUserId}")
    public ResponseEntity<String> unfollowTargetId(@PathVariable("socialId") Long socialId,
                                                   @PathVariable("targetUserId") Long targetUserId) {
        socialInteractionService.unfollowTargetUser(socialId, targetUserId);
        return ResponseEntity.ok(String.format("User %s has unfollowed user %s", socialId, targetUserId));
    }
    @DeleteMapping("/{socialId}/bookmark/{targetRecipeId}")
    public ResponseEntity<String> unbookmarkTargetRecipeId(@PathVariable("socialId") Long socialId,
                                                           @PathVariable("targetRecipeId") Long targetRecipeId) {
        socialInteractionService.unbookmarkTargetRecipe(socialId, targetRecipeId);
        return ResponseEntity.ok(String.format("User %s has unbookmarked recipe %s", socialId, targetRecipeId));
    }
}
