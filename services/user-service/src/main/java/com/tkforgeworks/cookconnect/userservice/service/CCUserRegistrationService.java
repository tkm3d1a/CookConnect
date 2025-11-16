package com.tkforgeworks.cookconnect.userservice.service;

import com.tkforgeworks.cookconnect.userservice.clients.KeycloakAdminClient;
import com.tkforgeworks.cookconnect.userservice.model.CCUser;
import com.tkforgeworks.cookconnect.userservice.model.ProfileInfo;
import com.tkforgeworks.cookconnect.userservice.model.dto.CCUserDto;
import com.tkforgeworks.cookconnect.userservice.model.dto.CCUserRegistrationRequestDto;
import com.tkforgeworks.cookconnect.userservice.model.mapper.UserServiceMapper;
import com.tkforgeworks.cookconnect.userservice.repository.CCUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CCUserRegistrationService {

    private final CCUserRepository ccUserRepository;
    private final KeycloakAdminClient kcClient;
    private final UserServiceMapper mapper;

    @Transactional
    public CCUserDto registerUser(CCUserRegistrationRequestDto requestDto) {
        if(ccUserRepository.existsByUsername(requestDto.username())){
            throw new RuntimeException("Username already exists");
        }
        if(ccUserRepository.existsByEmail(requestDto.email())){
            throw new RuntimeException("Email already exists");
        }

        log.info("Registering user {}", requestDto.username());
        String keycloakUserId;
        CCUser savedUser = null;

        try{
            keycloakUserId = kcClient.createUser(
                    requestDto.username(),
                    requestDto.email(),
                    requestDto.password(),
                    requestDto.firstName(),
                    requestDto.lastName()
            );
        } catch (Exception e){
            log.error("Error while creating keycloak user", e);
            throw new RuntimeException("User registration failed: " + e.getMessage());
        }

        try{
            CCUser ccUser = new CCUser();
            ccUser.setUsername(requestDto.username());
            ccUser.setEmail(requestDto.email());
            ccUser.setId(keycloakUserId);

            ProfileInfo profileInfo = new ProfileInfo();
            profileInfo.setFirstName(requestDto.firstName());
            profileInfo.setLastName(requestDto.lastName());

            ccUser.setProfileInfo(profileInfo);

            savedUser = ccUserRepository.save(ccUser);
            log.info("Saved user with id {}: Username {}", savedUser.getId(), savedUser.getUsername());

        } catch (Exception e){
            log.error("Error while saving user", e);
            log.error("Rolling back keycloak user creation");
            kcClient.deleteUser(keycloakUserId);
        }

        return mapper.ccUserToCCUserDto(savedUser);
    }

    public void deleteUser(String ccUserId) {
        log.info("Deleting user {}", ccUserId);
        kcClient.deleteUser(ccUserId);
        log.info("Deleted user {}", ccUserId);
    }
}
