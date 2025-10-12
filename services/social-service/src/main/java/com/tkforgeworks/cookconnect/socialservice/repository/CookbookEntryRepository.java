package com.tkforgeworks.cookconnect.socialservice.repository;

import com.tkforgeworks.cookconnect.socialservice.model.CookbookEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CookbookEntryRepository extends JpaRepository<CookbookEntry, Long> {
}
