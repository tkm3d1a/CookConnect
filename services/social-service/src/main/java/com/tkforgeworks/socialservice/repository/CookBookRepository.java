package com.tkforgeworks.socialservice.repository;

import com.tkforgeworks.socialservice.model.Cookbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CookBookRepository extends JpaRepository<Cookbook, Long> {
}
