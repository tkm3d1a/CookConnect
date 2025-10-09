package com.tkforgeworks.socialservice.repository;

import com.tkforgeworks.socialservice.model.CookBookNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CookBookNoteRepository extends JpaRepository<CookBookNote, Long> {
}
