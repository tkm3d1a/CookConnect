package com.tkforgeworks.cookconnect.recipeservice.service;

import com.tkforgeworks.cookconnect.recipeservice.model.Tag;
import com.tkforgeworks.cookconnect.recipeservice.model.TagList;
import com.tkforgeworks.cookconnect.recipeservice.model.TagListItem;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.TagDto;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.TagListDto;
import com.tkforgeworks.cookconnect.recipeservice.model.dto.TagListItemDto;
import com.tkforgeworks.cookconnect.recipeservice.model.mapper.RecipeServiceMapper;
import com.tkforgeworks.cookconnect.recipeservice.repository.TagListRepository;
import com.tkforgeworks.cookconnect.recipeservice.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final TagListRepository tagListRepository;
//    private final TagListItemRepository tagListItemRepository;
    private final RecipeServiceMapper mapper;

    //INTERNAL
    protected TagList createBlankList() {
        TagList tagList = new TagList();
        return tagListRepository.save(tagList);
    }

    protected TagList createTagList(TagListDto tagListDto) {
        TagList tagList = new TagList();

        for (TagListItemDto itemDto: tagListDto.listItems()){
            log.debug("saving Tag list item:\n\t{}", itemDto);
            Tag tag = findOrCreateTag(itemDto.tag());
            TagListItem tagListItem = new TagListItem();
            tagListItem.setTag(tag);
            tagList.addTag(tagListItem);
        }

        return tagListRepository.save(tagList);
    }

    //PRIVATE
    protected Tag findOrCreateTag(TagDto tagDto) {
        log.debug("findOrCreateTag\n\t{}", tagDto);
        return tagRepository.findByName(tagDto.name())
                .orElseGet(() -> tagRepository.save(mapper.toTag(tagDto)));
    }
}
