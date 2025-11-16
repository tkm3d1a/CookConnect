package com.tkforgeworks.cookconnect.userservice.controller;

import com.tkforgeworks.cookconnect.userservice.model.dto.CCUserDto;
import com.tkforgeworks.cookconnect.userservice.model.dto.CCUserRegistrationRequestDto;
import com.tkforgeworks.cookconnect.userservice.service.CCUserRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CCUserRegistrationController {
    private final CCUserRegistrationService ccUserRegistrationService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody CCUserRegistrationRequestDto requestDto) {
        try{
            CCUserDto user = ccUserRegistrationService.registerUser(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (RuntimeException ex){
            log.error("Registering user failed", ex);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

}
