package com.tkforgeworks.cookconnect.socialservice.service;

import com.tkforgeworks.cookconnect.socialservice.repository.CookbookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookbookService {
    private final CookbookRepository cookBookRepository;
    private final CookbookEntryService entryService;
    private final CookbookNoteService noteService;

}
