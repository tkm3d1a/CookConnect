package com.tkforgeworks.cookconnect.recipeservice.service;

import com.tkforgeworks.cookconnect.recipeservice.model.TagList;
import com.tkforgeworks.cookconnect.recipeservice.repository.TagListItemRepository;
import com.tkforgeworks.cookconnect.recipeservice.repository.TagListRepository;
import com.tkforgeworks.cookconnect.recipeservice.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final TagListRepository tagListRepository;
    private final TagListItemRepository tagListItemRepository;

    //INTERNAL
    protected TagList createBlankList() {
        TagList tagList = new TagList();
        return tagListRepository.save(tagList);
    }
}
