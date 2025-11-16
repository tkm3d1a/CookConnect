package com.tkforgeworks.cookconnect.socialservice.service;

import com.tkforgeworks.cookconnect.socialservice.clients.UserServiceFeignClient;
import com.tkforgeworks.cookconnect.socialservice.model.SocialInteraction;
import com.tkforgeworks.cookconnect.socialservice.model.dto.CookbookDto;
import com.tkforgeworks.cookconnect.socialservice.model.dto.SocialCreateResponseDto;
import com.tkforgeworks.cookconnect.socialservice.model.dto.SocialInteractionDto;
import com.tkforgeworks.cookconnect.socialservice.model.mapper.SocialInteractionMapper;
import com.tkforgeworks.cookconnect.socialservice.repository.SocialInteractionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialInteractionService {
    private final SocialInteractionRepository socialInteractionRepository;
    private final CookbookService cookBookService;
    private final UserServiceFeignClient userServiceFeignClient;
    private final SocialInteractionMapper mapper;

    public SocialInteractionDto getSocialProfile(Long socialId) {
        return mapper.toSocialInteractionDto(findOrThrow(socialId));
    }

    public List<Long> getFollowers(Long socialId) {
        SocialInteraction foundSI = findOrThrow(socialId);
        return foundSI.getFollowerIds().stream().toList();
    }

    public List<Long> getFollowing(Long socialId) {
        SocialInteraction foundSI = findOrThrow(socialId);
        return foundSI.getFollowingIds().stream().toList();
    }

    public List<Long> getBookmarks(Long socialId) {
        SocialInteraction foundSI = findOrThrow(socialId);
        return foundSI.getBookmarkedRecipeIds().stream().toList();
    }

    @Transactional
    public void createNewSocial(long forUserId) {
        if (socialInteractionRepository.existsSocialInteractionByForUserId(forUserId)) {
            throw new RuntimeException(String.format("Social Interaction already exists for user %s", forUserId));
        }
        SocialCreateResponseDto responseDto = userServiceFeignClient.getUserById(forUserId);
        userServiceFeignClient.addSocialInteraction(responseDto.id());
        SocialInteraction socialInteraction = new SocialInteraction();
        socialInteraction.setForUserId(responseDto.id());
        socialInteractionRepository.save(socialInteraction);
    }

    @Transactional
    public SocialInteractionDto followTargetUser(Long socialId, Long targetUserId) {
        SocialInteraction foundSI = findOrThrow(socialId);
        SocialInteraction foundSITargetUser = findOrThrow(targetUserId);

        if(foundSI.getFollowingIds().contains(foundSITargetUser.getForUserId())){
            throw new RuntimeException(String.format("All-ready following target user id %s", targetUserId));
        }

        foundSI.getFollowingIds().add(foundSITargetUser.getForUserId());
        foundSITargetUser.getFollowerIds().add(foundSI.getForUserId());

        socialInteractionRepository.save(foundSITargetUser);
        return mapper.toSocialInteractionDto(socialInteractionRepository.save(foundSI));
    }

    public SocialInteractionDto bookmarkTargetRecipe(Long socialId, Long targetRecipeId) {
        SocialInteraction foundSI = findOrThrow(socialId);
        if(!foundSI.getBookmarkedRecipeIds().add(targetRecipeId)){
            throw new RuntimeException(String.format("All-ready bookmarked recipe id %s", targetRecipeId));
        }
        return mapper.toSocialInteractionDto(socialInteractionRepository.save(foundSI));
    }

    public SocialInteractionDto createCookBookForSI(Long socialId, CookbookDto cookbookDto) {
        SocialInteraction foundSI = findOrThrow(socialId);
        foundSI.getCookbooks().add(mapper.toCookbook(cookBookService.createCookbook(cookbookDto)));
        return mapper.toSocialInteractionDto(socialInteractionRepository.save(foundSI));
    }

    @Transactional
    public void unfollowTargetUser(Long socialId, Long targetUserId) {
        SocialInteraction foundSi = findOrThrow(socialId);
        SocialInteraction foundSITargetUser = findOrThrow(targetUserId);
        if(
                !foundSi.getFollowingIds().remove(targetUserId) &&
                !foundSITargetUser.getFollowerIds().remove(foundSi.getForUserId())
        ){
            throw new RuntimeException(String.format("Not currently following target user id %s", targetUserId));
        }
        socialInteractionRepository.save(foundSITargetUser);
        socialInteractionRepository.save(foundSi);
    }

    public void unbookmarkTargetRecipe(Long socialId, Long targetRecipeId) {
        SocialInteraction foundSI = findOrThrow(socialId);
        if(!foundSI.getBookmarkedRecipeIds().remove(targetRecipeId)){
            throw new RuntimeException(String.format("Not currently bookmarked recipe id %s", targetRecipeId));
        }
        socialInteractionRepository.save(foundSI);
    }

    @Transactional
    public void removeSocialInteraction(Long forUserId) {
        SocialInteraction foundSI = findOrThrow(forUserId);
        userServiceFeignClient.removeSocialInteraction(foundSI.getForUserId());
        socialInteractionRepository.delete(foundSI);
    }
    /*
    PRIVATE helper methods only
        used to eliminate repetitive or long code sections used commonly
     */
    private SocialInteraction findOrThrow(Long socialId) {
        return socialInteractionRepository.findById(socialId).orElseThrow(() -> new RuntimeException("user not found"));
    }

}
