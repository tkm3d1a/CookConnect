package com.tkforgeworks.cookconnect.socialservice.controller;

import com.tkforgeworks.cookconnect.socialservice.service.SocialInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/social")
@RequiredArgsConstructor
public class SocialInteractionController {
    private final SocialInteractionService socialInteractionService;
}
