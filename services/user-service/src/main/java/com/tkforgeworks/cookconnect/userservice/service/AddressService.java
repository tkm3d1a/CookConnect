package com.tkforgeworks.cookconnect.userservice.service;

import com.tkforgeworks.cookconnect.userservice.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
}
