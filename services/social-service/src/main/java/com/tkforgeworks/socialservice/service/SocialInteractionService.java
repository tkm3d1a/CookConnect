package com.tkforgeworks.socialservice.service;

import com.tkforgeworks.socialservice.repository.SocialInteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialInteractionService {
    private final SocialInteractionRepository socialInteractionRepository;
    private final CookBookService cookBookService;
}
