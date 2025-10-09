package com.tkforgeworks.socialservice.repository;

import com.tkforgeworks.socialservice.model.CookBookEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CookBookEntryRepository extends JpaRepository<CookBookEntry, Long> {
}
