package com.tkforgeworks.cookconnect.socialservice.controller;

import com.tkforgeworks.cookconnect.socialservice.model.dto.CookbookDto;
import com.tkforgeworks.cookconnect.socialservice.model.dto.SocialInteractionDto;
import com.tkforgeworks.cookconnect.socialservice.service.SocialInteractionService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SocialInteractionController {
    private final SocialInteractionService socialInteractionService;

    //GET
    @GetMapping("/{socialId}")
    @RateLimiter(name = "main")
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
    @PostMapping("/{socialId}/create-cookbook")
    @RateLimiter(name = "main")
    public ResponseEntity<SocialInteractionDto> createCookbookForSI(@PathVariable("socialId") String socialId,
                                                                    @RequestBody CookbookDto cookbookDto) {
        return ResponseEntity.accepted().body(socialInteractionService.createCookBookForSI(socialId, cookbookDto));
    }

    @PostMapping("/{forUserId}")
    @PreAuthorize("hasRole('cookconnect_admin') or @authorizationHelper.canAccessUserResource(#forUserId)")
    @RateLimiter(name = "main")
    public ResponseEntity<?> createSocialInteraction(@PathVariable("forUserId") String forUserId) {
        log.debug("Received forUserId parameter: {}", forUserId);

        // Check method parameter names via reflection (for debugging)
        try {
            Method method = this.getClass().getMethod("createSocialInteraction", String.class);
            for (Parameter param : method.getParameters()) {
                log.debug("Parameter name: {}, Value: {}", param.getName(), forUserId);
            }
        } catch (Exception e) {
            log.error("Could not inspect method parameters", e);
        }
        socialInteractionService.createNewSocial(forUserId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    //PUT
    //DELETE
    @PreAuthorize("hasRole('cookconnect_admin') or @authorizationHelper.canAccessUserResource(#socialId)")
    @DeleteMapping("/{socialId}/follow/{targetUserId}")
    public ResponseEntity<String> unfollowTargetId(@PathVariable("socialId") String socialId,
                                                   @PathVariable("targetUserId") String targetUserId) {
        socialInteractionService.unfollowTargetUser(socialId, targetUserId);
        return ResponseEntity.ok(String.format("User %s has unfollowed user %s", socialId, targetUserId));
    }
    @PreAuthorize("hasRole('cookconnect_admin') or @authorizationHelper.canAccessUserResource(#socialId)")
    @DeleteMapping("/{socialId}/bookmark/{targetRecipeId}")
    public ResponseEntity<String> unbookmarkTargetRecipeId(@PathVariable("socialId") String socialId,
                                                           @PathVariable("targetRecipeId") Long targetRecipeId) {
        socialInteractionService.unbookmarkTargetRecipe(socialId, targetRecipeId);
        return ResponseEntity.ok(String.format("User %s has unbookmarked recipe %s", socialId, targetRecipeId));
    }
    @PreAuthorize("hasRole('cookconnect_admin') or @authorizationHelper.canAccessUserResource(#forUserId)")
    @DeleteMapping("/{forUserId}")
    public ResponseEntity<?> removeSocialInteraction(@PathVariable("forUserId") String forUserId) {
        socialInteractionService.removeSocialInteraction(forUserId);
        return ResponseEntity.ok().build();
    }
}
