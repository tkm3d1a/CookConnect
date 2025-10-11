package com.tkforgeworks.userservice.repository;

import com.tkforgeworks.userservice.model.ProfileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileInfoRepository extends JpaRepository<ProfileInfo, Long> {
}
