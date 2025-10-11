package com.tkforgeworks.socialservice.service;

import com.tkforgeworks.socialservice.repository.CookBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookBookService {
    private final CookBookRepository cookBookRepository;
    private final CookBookEntryService entryService;
    private final CookBookNoteService noteService;

}
