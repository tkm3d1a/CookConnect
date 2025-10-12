package com.tkforgeworks.cookconnect.userservice.model.mapper;

import com.tkforgeworks.cookconnect.userservice.model.Address;
import com.tkforgeworks.cookconnect.userservice.model.CCUser;
import com.tkforgeworks.cookconnect.userservice.model.ProfileInfo;
import com.tkforgeworks.cookconnect.userservice.model.dto.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserServiceMapper {
    CCUserDto ccUserToCCUserDto(CCUser ccUser);
    AddressDto addressToAddressDto(Address address);
    ProfileInfoDto profileInfoToProfileInfoDto(ProfileInfo profileInfo);

    List<CCUserDto> ccUsersToCCUserDtos(List<CCUser> ccUsers);
    List<AddressDto> addressesToAddressDtos(List<Address> addresses);
    List<ProfileInfoDto> profileInfosToProfileInfoDtos(List<ProfileInfo> profileInfos);

    @Mapping(target = "password", ignore = true)
    CCUser ccUserDtoToCCUser(CCUserDto ccUserDto);
    Address addressDtoToAddress(AddressDto addressDto);
    ProfileInfo profileInfoDtoToProfileInfo(ProfileInfoDto profileInfoDto);

    NoProfileCCUserDTO ccUserDtoToNoProfileCCUserDTO(CCUserDto ccUserDto);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "profileInfo", ignore = true)
    CCUser NoProfileCCUserDTOToCCUser(NoProfileCCUserDTO noProfileCCUserDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "profileInfo", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCCUser(UpdateCCUserDTO ccUserDto, @MappingTarget CCUser ccUser);
}
