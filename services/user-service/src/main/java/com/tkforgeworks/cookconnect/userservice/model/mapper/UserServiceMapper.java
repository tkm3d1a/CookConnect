package com.tkforgeworks.cookconnect.userservice.model.mapper;

import com.tkforgeworks.cookconnect.userservice.model.Address;
import com.tkforgeworks.cookconnect.userservice.model.CCUser;
import com.tkforgeworks.cookconnect.userservice.model.ProfileInfo;
import com.tkforgeworks.cookconnect.userservice.model.dto.AddressDto;
import com.tkforgeworks.cookconnect.userservice.model.dto.CCUserDto;
import com.tkforgeworks.cookconnect.userservice.model.dto.ProfileInfoDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserServiceMapper {
    CCUserDto ccUserToCCUserDto(CCUser ccUser);
    AddressDto addressToAddressDto(Address address);
    ProfileInfoDto profileInfoToProfileInfoDto(ProfileInfo profileInfo);

    List<CCUserDto> ccUsersToCCUserDtos(List<CCUser> ccUsers);
    List<AddressDto> addressesToAddressDtos(List<Address> addresses);
    List<ProfileInfoDto> profileInfosToProfileInfoDtos(List<ProfileInfo> profileInfos);

    CCUser ccUserDtoToCCUser(CCUserDto ccUserDto);
    Address addressDtoToAddress(AddressDto addressDto);
    ProfileInfo profileInfoDtoToProfileInfo(ProfileInfo profileInfo);
}
