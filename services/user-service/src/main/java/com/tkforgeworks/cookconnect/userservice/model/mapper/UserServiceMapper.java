package com.tkforgeworks.cookconnect.userservice.model.mapper;

import com.tkforgeworks.cookconnect.userservice.model.CCUser;
import com.tkforgeworks.cookconnect.userservice.model.ProfileInfo;
import com.tkforgeworks.cookconnect.userservice.model.dto.CCUserDto;
import com.tkforgeworks.cookconnect.userservice.model.dto.NoProfileCCUserDTO;
import com.tkforgeworks.cookconnect.userservice.model.dto.ProfileInfoDto;
import com.tkforgeworks.cookconnect.userservice.model.dto.UpdateCCUserDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserServiceMapper {
    CCUserDto ccUserToCCUserDto(CCUser ccUser);

    List<CCUserDto> ccUsersToCCUserDtos(List<CCUser> ccUsers);

    ProfileInfo profileInfoDtoToProfileInfo(ProfileInfoDto profileInfoDto);

    NoProfileCCUserDTO ccUserDtoToNoProfileCCUserDTO(CCUserDto ccUserDto);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profileInfo", ignore = true)
    CCUser NoProfileCCUserDTOToCCUser(NoProfileCCUserDTO noProfileCCUserDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profileInfo", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCCUser(UpdateCCUserDTO ccUserDto, @MappingTarget CCUser ccUser);
}
