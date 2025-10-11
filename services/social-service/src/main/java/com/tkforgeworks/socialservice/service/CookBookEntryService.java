package com.tkforgeworks.socialservice.service;

import com.tkforgeworks.socialservice.repository.CookBookEntryRepository;
import com.tkforgeworks.socialservice.repository.EntryNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookBookEntryService {
    private final CookBookEntryRepository cookBookEntryRepository;
    private final EntryNoteRepository entryNoteRepository;
}
