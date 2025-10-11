package com.tkforgeworks.cookconnect.socialservice.service;

import com.tkforgeworks.cookconnect.socialservice.repository.CookBookEntryRepository;
import com.tkforgeworks.cookconnect.socialservice.repository.EntryNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookBookEntryService {
    private final CookBookEntryRepository cookBookEntryRepository;
    private final EntryNoteRepository entryNoteRepository;
}
