package com.tkforgeworks.recipeservice.service;

import com.tkforgeworks.recipeservice.repository.RecipeNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipeNoteService {
    private final RecipeNoteRepository recipeNoteRepository;
}
