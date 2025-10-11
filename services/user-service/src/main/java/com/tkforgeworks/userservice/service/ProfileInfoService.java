package com.tkforgeworks.userservice.service;

import com.tkforgeworks.userservice.repository.ProfileInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileInfoService {
    private final ProfileInfoRepository profileInfoRepository;
    private final AddressService addressService;
}
