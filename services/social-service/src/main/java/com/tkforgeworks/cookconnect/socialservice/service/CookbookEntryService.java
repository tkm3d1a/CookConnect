package com.tkforgeworks.cookconnect.socialservice.service;

import com.tkforgeworks.cookconnect.socialservice.repository.CookbookEntryRepository;
import com.tkforgeworks.cookconnect.socialservice.repository.EntryNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookbookEntryService {
    private final CookbookEntryRepository cookBookEntryRepository;
    private final EntryNoteRepository entryNoteRepository;
}
