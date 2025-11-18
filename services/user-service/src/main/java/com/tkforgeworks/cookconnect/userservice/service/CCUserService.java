package com.tkforgeworks.cookconnect.userservice.service;

import com.tkforgeworks.cookconnect.userservice.errorhandler.UserNotFoundException;
import com.tkforgeworks.cookconnect.userservice.model.CCUser;
import com.tkforgeworks.cookconnect.userservice.model.dto.CCUserDto;
import com.tkforgeworks.cookconnect.userservice.model.dto.NoProfileCCUserDTO;
import com.tkforgeworks.cookconnect.userservice.model.dto.UpdateCCUserDTO;
import com.tkforgeworks.cookconnect.userservice.model.mapper.UserServiceMapper;
import com.tkforgeworks.cookconnect.userservice.repository.CCUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CCUserService {
    private final CCUserRepository userRepository;
    private final ProfileInfoService profileInfoService;
    private final CCUserRegistrationService userRegistrationService;
    private final UserServiceMapper mapper;

    public CCUserDto createUser(CCUserDto ccUserDto) {
        if(ccUserDto.id() != null){
            //checks if the user was passed with an id
            if(userRepository.existsById(ccUserDto.id())){
                log.error("User with id {} already exists", ccUserDto.id());
                throw new RuntimeException(String.format("User with id %s already exists", ccUserDto.id()));
            }
            if(!Objects.equals(ccUserDto.profileInfo().id(), ccUserDto.id())){
                log.error("User with id {} does not match profile info id {}", ccUserDto.id(), ccUserDto.profileInfo().id());
                log.error("Passed user:\n{}", ccUserDto);
                throw new RuntimeException(String.format("User with id %s does not match profile info id %s", ccUserDto.id(), ccUserDto.profileInfo().id()));
            }
        }

        NoProfileCCUserDTO initCCUserDTO = mapper.ccUserDtoToNoProfileCCUserDTO(ccUserDto);
        CCUser ccUserInit = userRepository.save(mapper.NoProfileCCUserDTOToCCUser(initCCUserDTO));
        ccUserInit.setProfileInfo(
                profileInfoService.addProfileToUser(ccUserInit, ccUserDto.profileInfo())
        );

        CCUser createdUser = userRepository.save(ccUserInit);
        return mapper.ccUserToCCUserDto(createdUser);
    }

    public CCUserDto findUser(String ccUserId) {
        Optional<CCUser> ccUser = userRepository.findById(ccUserId);
        return ccUser.map(mapper::ccUserToCCUserDto).orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", ccUserId)));
    }

    public List<CCUserDto> getAllUsers() {
        return mapper.ccUsersToCCUserDtos(userRepository.findAll());
    }

    public CCUserDto updateUser(String ccUserId, UpdateCCUserDTO updateCCUserDTO) {
        if(!Objects.equals(updateCCUserDTO.id(), ccUserId)){
            log.error("User with id {} does not match id {}", updateCCUserDTO.id(), ccUserId);
            throw new RuntimeException(String.format("User with id %s does not match id %s", updateCCUserDTO.id(), ccUserId));
        }
        CCUser userToUpdate = userRepository.findById(updateCCUserDTO.id())
                .orElseThrow(() -> new RuntimeException("User not found"));

        mapper.updateCCUser(updateCCUserDTO, userToUpdate);
        CCUser updatedUser = userRepository.save(userToUpdate);
        return mapper.ccUserToCCUserDto(updatedUser);
    }

    @Transactional
    public String deleteUserById(String ccUserId) {
        try{
            String foundUserId = userRepository
                    .findById(ccUserId)
                    .orElseThrow(() -> new RuntimeException(String.format("User with id %s not found", ccUserId)))
                    .getId();
            userRegistrationService.deleteUser(foundUserId);
            userRepository.deleteById(ccUserId);
        } catch(Exception ex){
            log.error("User with id {} could not be deleted", ccUserId);
            throw new RuntimeException(String.format("User with id %s could not be deleted", ccUserId));
        }
        return "User with id " + ccUserId + " deleted";
    }

    public void updateSocial(String ccUserId, boolean hasSocial) {
        CCUser foundUser = userRepository.findById(ccUserId).orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", ccUserId)));
        foundUser.setHasSocialInteraction(hasSocial);
        userRepository.save(foundUser);
    }
}
