package com.tkforgeworks.cookconnect.socialservice.service;

import com.tkforgeworks.cookconnect.socialservice.model.Cookbook;
import com.tkforgeworks.cookconnect.socialservice.model.dto.CookbookDto;
import com.tkforgeworks.cookconnect.socialservice.model.dto.CookbookEntryDto;
import com.tkforgeworks.cookconnect.socialservice.model.dto.CookbookNoteDto;
import com.tkforgeworks.cookconnect.socialservice.model.mapper.SocialInteractionMapper;
import com.tkforgeworks.cookconnect.socialservice.repository.CookbookNoteRepository;
import com.tkforgeworks.cookconnect.socialservice.repository.CookbookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CookbookService {
    private final CookbookRepository cookbookRepository;
    private final CookbookNoteRepository cookBookNoteRepository;
    private final CookbookEntryService entryService;
    private final SocialInteractionMapper mapper;

    public List<CookbookDto> getAllCookbooks() {
        return mapper.toCookbookDtoList(cookbookRepository.findAll());
    }

    public CookbookDto getCookbookById(Long cookbookId) {
        throw new RuntimeException("not yet implemented");
    }

    public List<CookbookEntryDto> getCookbookEntries(Long cookbookId) {
        throw new RuntimeException("not yet implemented");
    }

    public CookbookEntryDto getCookbookEntryById(Long cookbookId, Long entryId) {
        throw new RuntimeException("not yet implemented");
    }

    public CookbookNoteDto getCookbookNote(Long cookbookId) {
        throw new RuntimeException("not yet implemented");
    }

    public CookbookDto createCookbook(CookbookDto cookbookDto) {
        if(Objects.nonNull(cookbookDto.id()) && cookbookRepository.existsById(cookbookDto.id())){
            throw new RuntimeException("cookbook already exists");
        }
        Cookbook cookbook = mapper.toCookbook(cookbookDto);
        Cookbook persistedCookbook = cookbookRepository.save(cookbook);
        return mapper.toCookbookDto(persistedCookbook);
    }

    public CookbookEntryDto addCookbookEntry(Long cookbookId, CookbookEntryDto cookbookEntryDto) {
        throw new RuntimeException("not yet implemented");
    }

    public CookbookNoteDto addCookbookNote(Long cookbookId, CookbookNoteDto cookbookNoteDto) {
        throw new RuntimeException("not yet implemented");
    }

    public CookbookDto updateCookBook(Long cookbookId, CookbookDto cookbookDto) {
        throw new RuntimeException("not yet implemented");
    }

    public CookbookEntryDto updateCookbookEntry(Long cookbookId, Long entryId, CookbookEntryDto cookbookEntryDto) {
        throw new RuntimeException("not yet implemented");
    }

    public CookbookNoteDto updateCookbookNote(Long cookbookId, CookbookNoteDto cookbookNoteDto) {
        throw new RuntimeException("not yet implemented");
    }

    public void deleteCookbook(Long cookbookId) {
        throw new RuntimeException("not yet implemented");
    }

    public void deleteCookbookEntries(Long cookbookId) {
        throw new RuntimeException("not yet implemented");
    }

    public void deleteCookbookEntryById(Long cookbookId, Long entryId) {
        throw new RuntimeException("not yet implemented");
    }

    public void deleteCookbookNote(Long cookbookId) {
        throw new RuntimeException("not yet implemented");
    }
}
