package com.tkforgeworks.cookconnect.recipeservice.service;

import com.tkforgeworks.cookconnect.recipeservice.clients.UserServiceFeignClient;
import com.tkforgeworks.cookconnect.recipeservice.common.dto.UserServiceResponseDto;
import com.tkforgeworks.cookconnect.recipeservice.message.model.UserChangeEvent;
import com.tkforgeworks.cookconnect.recipeservice.model.Recipe;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeCreateDetailedRequestDto;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeCreateSimpleRequestDto;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeDto;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.RecipeSummaryDto;
import com.tkforgeworks.cookconnect.recipeservice.model.mapper.RecipeServiceMapper;
import com.tkforgeworks.cookconnect.recipeservice.repository.RecipeRepository;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
//    private final RecipeNoteService recipeNoteService;
    private final TagService tagService;
    private final IngredientService ingredientService;
    private final InstructionService instructionService;
    private final RecipeServiceMapper mapper;
    private final UserServiceFeignClient  userServiceFeignClient;

    //GET
    public Page<RecipeSummaryDto> getAllRecipesSummary(Pageable pageable) {
        return recipeRepository.findAll(pageable).map(mapper::toRecipeSummaryDto);
    }

    //POST
    public RecipeDto createSimpleRecipe(RecipeCreateSimpleRequestDto recipeCreateSimpleRequestDto) {
        UserServiceResponseDto fetchedUser = getUserExt(recipeCreateSimpleRequestDto.createdBy());
        Recipe toCreate = mapper.toRecipeFromCreateSimple(recipeCreateSimpleRequestDto);
        toCreate.setCreatedBy(fetchedUser.id());
        toCreate.setCreatedByUsername(fetchedUser.username());
        toCreate.setIngredientList(ingredientService.createBlankList());
        toCreate.setInstructionList(instructionService.createBlankList());
        toCreate.setTagList(tagService.createBlankList());

        return mapper.toRecipeDto(recipeRepository.save(toCreate));
    }

    public RecipeDto createdDetailedRecipe(RecipeCreateDetailedRequestDto recipeCreateDetailedRequestDto) {
        log.debug("CreateDetailedRecipe:\n\t{}", recipeCreateDetailedRequestDto);
        UserServiceResponseDto fetchedUser = getUserExt(recipeCreateDetailedRequestDto.createdBy());
        Recipe toCreate = mapper.toRecipeFromCreateDetailed(recipeCreateDetailedRequestDto);
        toCreate.setCreatedBy(fetchedUser.id());
        toCreate.setCreatedByUsername(fetchedUser.username());

        if(recipeCreateDetailedRequestDto.ingredientList() == null){
            toCreate.setIngredientList(ingredientService.createBlankList());
        } else {
            toCreate.setIngredientList(ingredientService.createIngredientList(recipeCreateDetailedRequestDto.ingredientList()));
        }
        if(recipeCreateDetailedRequestDto.instructionList() == null){
            toCreate.setInstructionList(instructionService.createBlankList());
        } else {
            toCreate.setInstructionList(instructionService.createInstructionList(recipeCreateDetailedRequestDto.instructionList()));
        }
        if(recipeCreateDetailedRequestDto.tagList() == null){
            toCreate.setTagList(tagService.createBlankList());
        } else {
            toCreate.setTagList(tagService.createTagList(recipeCreateDetailedRequestDto.tagList()));
        }

        return mapper.toRecipeDto(recipeRepository.save(toCreate));
    }
    //PUT
    //DELETE
    //PRIVATE - UTILITY
    private UserServiceResponseDto createAnonymousUser(){
        return new UserServiceResponseDto("anonymous","anonymous",false,false);
    }

    @CircuitBreaker(name = "main", fallbackMethod = "fallbackUserServiceGetExt")
    @Retry(name = "main")
    private UserServiceResponseDto getUserExt(String ccUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return createAnonymousUser();
        }

        try{
            return userServiceFeignClient.getUserById(ccUserId);
        } catch(FeignException.NotFound e) {
            return createAnonymousUser();
        }
    }

    private UserServiceResponseDto fallbackUserServiceGetExt(String ccUserId, Exception e) {
        log.error("fallbackUserServiceGetExt");
        throw new RuntimeException(e.getMessage());
    }

    //MESSAGE HANDLING
    public void handleUserAccountStatus(UserChangeEvent event, String status) {
        log.debug("handling update to user account status: userId - {}, status - {}",
                event.getUserId(),
                status);
        List<Recipe> usersRecipes = recipeRepository.findRecipeByCreatedBy(event.getUserId());
        if(usersRecipes.isEmpty()){
            log.debug("No Recipes found for user with id {}", event.getUserId());
            return;
        }
        switch (status) {
            case "private" -> {
                log.warn("Privacy status change - not currently handled");
            }
            case "closed" -> {
                log.debug("Changing userName on each recipe");
                for(Recipe recipe : usersRecipes){
                    String oldName = recipe.getCreatedByUsername();
                    String newName = oldName + " (CLOSED)";
                    recipe.setCreatedByUsername(newName);
                    log.debug("Changing userName on recipe {}", recipe.getId());
                    recipeRepository.save(recipe);
                }
            }
            case "delete" -> {
                log.debug("Removing userName and userId on each recipe");
                for(Recipe recipe : usersRecipes){
                    recipe.setCreatedBy("anonymous");
                    recipe.setCreatedByUsername("anonymous");
                    log.debug("Changing userName and userId on recipe {}", recipe.getId());
                    recipeRepository.save(recipe);
                }
            }
            default -> {
                log.warn("Unknown status - {}", status);
            }
        }
    }
}