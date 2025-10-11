package com.tkforgeworks.cookconnect.userservice.repository;

import com.tkforgeworks.cookconnect.userservice.model.CCUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CCUserRepository extends JpaRepository<CCUser, Long> {
}
