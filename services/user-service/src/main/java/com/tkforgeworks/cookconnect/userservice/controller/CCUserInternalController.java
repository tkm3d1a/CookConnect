package com.tkforgeworks.cookconnect.userservice.controller;

import com.tkforgeworks.cookconnect.userservice.model.dto.SocialCreateResponseDto;
import com.tkforgeworks.cookconnect.userservice.model.mapper.UserServiceMapper;
import com.tkforgeworks.cookconnect.userservice.service.CCUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/internal")
@RequiredArgsConstructor
public class CCUserInternalController {
    private final CCUserService ccUserService;
    private final UserServiceMapper mapper;

    @GetMapping("/{userId}")
    public ResponseEntity<SocialCreateResponseDto> getCCUser(@PathVariable("userId") Long ccUserId) {
        return ResponseEntity.ok(mapper.ccUserDtoToSocialCreateResponseDto(ccUserService.findUser(ccUserId)));
    }

    @PostMapping("/{userId}/social")
    public ResponseEntity<?> addSocial(@PathVariable("userId") long ccUserId){
        ccUserService.updateSocial(ccUserId, true);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/social")
    public ResponseEntity<?> removeSocial(@PathVariable("userId") long ccUserId){
        ccUserService.updateSocial(ccUserId, false);
        return ResponseEntity.ok().build();
    }
}
