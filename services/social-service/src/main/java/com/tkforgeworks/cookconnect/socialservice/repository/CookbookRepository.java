package com.tkforgeworks.cookconnect.socialservice.repository;

import com.tkforgeworks.cookconnect.socialservice.model.Cookbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CookbookRepository extends JpaRepository<Cookbook, Long> {
}
