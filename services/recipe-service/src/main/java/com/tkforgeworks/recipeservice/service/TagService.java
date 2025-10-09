package com.tkforgeworks.recipeservice.service;

import com.tkforgeworks.recipeservice.repository.TagListItemRepository;
import com.tkforgeworks.recipeservice.repository.TagListRepository;
import com.tkforgeworks.recipeservice.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final TagListRepository tagListRepository;
    private final TagListItemRepository tagListItemRepository;
}
