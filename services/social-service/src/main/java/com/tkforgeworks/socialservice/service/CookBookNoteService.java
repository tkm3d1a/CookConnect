package com.tkforgeworks.socialservice.service;

import com.tkforgeworks.socialservice.repository.CookBookNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookBookNoteService {
    private final CookBookNoteRepository cookBookNoteRepository;
}
