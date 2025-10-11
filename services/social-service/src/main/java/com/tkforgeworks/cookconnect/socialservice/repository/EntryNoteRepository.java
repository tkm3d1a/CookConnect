package com.tkforgeworks.cookconnect.socialservice.repository;

import com.tkforgeworks.cookconnect.socialservice.model.EntryNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntryNoteRepository extends JpaRepository<EntryNote, Long> {
}
