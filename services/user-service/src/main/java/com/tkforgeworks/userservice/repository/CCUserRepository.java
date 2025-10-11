package com.tkforgeworks.userservice.repository;

import com.tkforgeworks.userservice.model.CCUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CCUserRepository extends JpaRepository<CCUser, Long> {
}
