package com.tkforgeworks.cookconnect.socialservice.controller;

import com.tkforgeworks.cookconnect.socialservice.service.CookbookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cookbook")
@RequiredArgsConstructor
public class CookbookController {
    private final CookbookService cookbookService;
}
