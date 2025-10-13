package com.tkforgeworks.cookconnect.socialservice.repository;

import com.tkforgeworks.cookconnect.socialservice.model.CookbookNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CookbookNoteRepository extends JpaRepository<CookbookNote, Long> {
}
