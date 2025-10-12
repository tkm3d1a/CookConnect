package com.tkforgeworks.cookconnect.userservice.service;

import com.tkforgeworks.cookconnect.userservice.model.CCUser;
import com.tkforgeworks.cookconnect.userservice.model.dto.CCUserDto;
import com.tkforgeworks.cookconnect.userservice.model.dto.NoProfileCCUserDTO;
import com.tkforgeworks.cookconnect.userservice.model.dto.UpdateCCUserDTO;
import com.tkforgeworks.cookconnect.userservice.model.mapper.UserServiceMapper;
import com.tkforgeworks.cookconnect.userservice.repository.CCUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CCUserService {
    private final CCUserRepository userRepository;
    private final ProfileInfoService profileInfoService;
    private final UserServiceMapper mapper;

    public CCUserDto createUser(CCUserDto ccUserDto) {
        if(ccUserDto.id() != null){
            //checks if the user was passed with an id
            if(userRepository.existsById(ccUserDto.id())){
                //pass user has ID and already exists in db
                //TODO: UserExists Error
                log.error("User with id {} already exists", ccUserDto.id());
                throw new RuntimeException(String.format("User with id %s already exists", ccUserDto.id()));
            }
            if(!Objects.equals(ccUserDto.profileInfo().id(), ccUserDto.id())){
                //user does not exist, but passed with ID that does not match profile info
                //TODO: BadDTOFormat Error
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

    public CCUserDto findUser(Long ccUserId) {
        Optional<CCUser> ccUser = userRepository.findById(ccUserId);
        return ccUser.map(mapper::ccUserToCCUserDto).orElseThrow(() -> new RuntimeException(String.format("User with id %s not found", ccUserId)));
    }

    public List<CCUserDto> getAllUsers() {
        return mapper.ccUsersToCCUserDtos(userRepository.findAll());
    }

    public CCUserDto updateUser(Long ccUserId, UpdateCCUserDTO updateCCUserDTO) {
        if(!Objects.equals(updateCCUserDTO.id(), ccUserId)){
            //TODO: BadRequestError
            log.error("User with id {} does not match id {}", updateCCUserDTO.id(), ccUserId);
            return null;
        }
        CCUser userToUpdate = userRepository.findById(updateCCUserDTO.id())
                .orElseThrow(() -> new RuntimeException("User not found"));

        mapper.updateCCUser(updateCCUserDTO, userToUpdate);
        CCUser updatedUser = userRepository.save(userToUpdate);
        return mapper.ccUserToCCUserDto(updatedUser);
    }

    public String deleteUserById(Long ccUserId) {
        //TODO: add ifExists check prior to delete for better response message
        userRepository.deleteById(ccUserId);
        return "User with id " + ccUserId + " deleted";
    }
}
