package com.tkforgeworks.cookconnect.socialservice.controller;

import com.tkforgeworks.cookconnect.socialservice.model.dto.CookbookDto;
import com.tkforgeworks.cookconnect.socialservice.model.dto.SocialInteractionDto;
import com.tkforgeworks.cookconnect.socialservice.service.SocialInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SocialInteractionController {
    private final SocialInteractionService socialInteractionService;

    //GET
    @GetMapping("/{socialId}")
    public ResponseEntity<SocialInteractionDto> getSocialInteraction(@PathVariable("socialId") String socialId) {
        return ResponseEntity.ok(socialInteractionService.getSocialProfile(socialId));
    }
    @GetMapping("/{socialId}/followers")
    public ResponseEntity<List<String>> getFollowers(@PathVariable("socialId") String socialId) {
        return ResponseEntity.ok(socialInteractionService.getFollowers(socialId));
    }
    @GetMapping("/{socialId}/following")
    public ResponseEntity<List<String>> getFollowing(@PathVariable("socialId") String socialId) {
        return ResponseEntity.ok(socialInteractionService.getFollowing(socialId));
    }
    @GetMapping("/{socialId}/bookmarks")
    public ResponseEntity<List<Long>> getBookmarks(@PathVariable("socialId") String socialId) {
        return ResponseEntity.ok(socialInteractionService.getBookmarks(socialId));
    }
    //POST
    @PostMapping("/{socialId}/follow/{targetUserId}")
    public ResponseEntity<SocialInteractionDto> followTargetId(@PathVariable("socialId") String socialId,
                                                               @PathVariable("targetUserId") String targetUserId) {
        return ResponseEntity.accepted().body(socialInteractionService.followTargetUser(socialId, targetUserId));
    }
    @PostMapping("/{socialId}/bookmark/{targetRecipeId}")
    public ResponseEntity<SocialInteractionDto> bookmarkTargetRecipe(@PathVariable("socialId") String socialId,
                                                                     @PathVariable("targetRecipeId") long targetRecipeId) {
        return ResponseEntity.accepted().body(socialInteractionService.bookmarkTargetRecipe(socialId, targetRecipeId));
    }
    @PostMapping("/{socialId}/create-cookbook") //TODO: Migrate to cookbooks controller
    public ResponseEntity<SocialInteractionDto> createCookbookForSI(@PathVariable("socialId") String socialId,
                                                                    @RequestBody CookbookDto cookbookDto) {
        return ResponseEntity.accepted().body(socialInteractionService.createCookBookForSI(socialId, cookbookDto));
    }

    @PostMapping("/{forUserId}")
    public ResponseEntity<?> createSocialInteraction(@PathVariable("forUserId") String forUserId) {
        socialInteractionService.createNewSocial(forUserId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    //PUT
    //DELETE
    @DeleteMapping("/{socialId}/follow/{targetUserId}")
    public ResponseEntity<String> unfollowTargetId(@PathVariable("socialId") String socialId,
                                                   @PathVariable("targetUserId") String targetUserId) {
        socialInteractionService.unfollowTargetUser(socialId, targetUserId);
        return ResponseEntity.ok(String.format("User %s has unfollowed user %s", socialId, targetUserId));
    }
    @DeleteMapping("/{socialId}/bookmark/{targetRecipeId}")
    public ResponseEntity<String> unbookmarkTargetRecipeId(@PathVariable("socialId") String socialId,
                                                           @PathVariable("targetRecipeId") Long targetRecipeId) {
        socialInteractionService.unbookmarkTargetRecipe(socialId, targetRecipeId);
        return ResponseEntity.ok(String.format("User %s has unbookmarked recipe %s", socialId, targetRecipeId));
    }
    @DeleteMapping("/{forUserId}")
    public ResponseEntity<?> removeSocialInteraction(@PathVariable("forUserId") String forUserId) {
        socialInteractionService.removeSocialInteraction(forUserId);
        return ResponseEntity.ok().build();
    }
}
