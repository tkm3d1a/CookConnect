package com.tkforgeworks.cookconnect.userservice.repository;

import com.tkforgeworks.cookconnect.userservice.model.ProfileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileInfoRepository extends JpaRepository<ProfileInfo, Long> {
}
