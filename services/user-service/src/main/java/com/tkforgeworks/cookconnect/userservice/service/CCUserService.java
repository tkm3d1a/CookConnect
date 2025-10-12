package com.tkforgeworks.cookconnect.userservice.service;

import com.tkforgeworks.cookconnect.userservice.model.CCUser;
import com.tkforgeworks.cookconnect.userservice.model.dto.CCUserDto;
import com.tkforgeworks.cookconnect.userservice.model.mapper.UserServiceMapper;
import com.tkforgeworks.cookconnect.userservice.repository.CCUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CCUserService {
    private final CCUserRepository userRepository;
    private final ProfileInfoService profileInfoService;
    private final UserServiceMapper mapper;

    public CCUserDto createUser(CCUserDto ccUserDto) {
        CCUser ccUser = mapper.ccUserDtoToCCUser(ccUserDto);
        if (ccUser == null) {
            return null;
        }

        if (ccUser.getId() != null && userRepository.existsById(ccUser.getId())) {
            return mapper.ccUserToCCUserDto(ccUser);
        }

        CCUser createdUser = userRepository.save(ccUser);
        return mapper.ccUserToCCUserDto(createdUser);
    }

    public CCUserDto findUser(Long ccUserId) {
        Optional<CCUser> ccUser = userRepository.findById(ccUserId);
        return ccUser.map(mapper::ccUserToCCUserDto).orElse(null);
    }

    public List<CCUserDto> getAllUsers() {
        return mapper.ccUsersToCCUserDtos(userRepository.findAll());
    }
}
