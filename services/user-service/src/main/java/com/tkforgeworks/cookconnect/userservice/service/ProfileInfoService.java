package com.tkforgeworks.cookconnect.userservice.service;

import com.tkforgeworks.cookconnect.userservice.model.CCUser;
import com.tkforgeworks.cookconnect.userservice.model.ProfileInfo;
import com.tkforgeworks.cookconnect.userservice.model.dto.ProfileInfoDto;
import com.tkforgeworks.cookconnect.userservice.model.mapper.UserServiceMapper;
import com.tkforgeworks.cookconnect.userservice.repository.ProfileInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileInfoService {
    private final ProfileInfoRepository profileInfoRepository;
    private final AddressService addressService;
    private final UserServiceMapper mapper;

    public ProfileInfo addProfileToUser(CCUser user, ProfileInfoDto profileInfoDto) {
        ProfileInfo pInfo = mapper.profileInfoDtoToProfileInfo(profileInfoDto);
        pInfo.setUser(user);
        if(!profileInfoDto.addresses().isEmpty()){
            pInfo.getAddresses().forEach(address -> address.setProfileInfo(pInfo));
        }
        return pInfo;
    }
}
