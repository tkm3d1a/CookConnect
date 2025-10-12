package com.tkforgeworks.cookconnect.socialservice.service;

import com.tkforgeworks.cookconnect.socialservice.repository.SocialInteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialInteractionService {
    private final SocialInteractionRepository socialInteractionRepository;
    private final CookbookService cookBookService;
}
