package com.tkforgeworks.cookconnect.userservice.service;

import com.tkforgeworks.cookconnect.userservice.repository.CCUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CCUserService {
    private final CCUserRepository userRepository;
    private final ProfileInfoService profileInfoService;
}
