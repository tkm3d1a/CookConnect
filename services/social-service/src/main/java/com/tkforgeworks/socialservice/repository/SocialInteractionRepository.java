package com.tkforgeworks.socialservice.repository;

import com.tkforgeworks.socialservice.model.SocialInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialInteractionRepository extends JpaRepository<SocialInteraction, Long> {
}
