package com.tkforgeworks.cookconnect.userservice.repository;

import com.tkforgeworks.cookconnect.userservice.model.CCUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CCUserRepository extends JpaRepository<CCUser, String> {
    Optional<CCUser> findByUsername(String username);
    Optional<CCUser> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
