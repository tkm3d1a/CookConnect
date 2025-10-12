package com.tkforgeworks.cookconnect.socialservice.service;

import com.tkforgeworks.cookconnect.socialservice.repository.CookbookNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookbookNoteService {
    private final CookbookNoteRepository cookBookNoteRepository;
}
