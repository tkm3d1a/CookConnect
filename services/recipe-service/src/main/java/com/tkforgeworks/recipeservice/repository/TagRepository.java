package com.tkforgeworks.recipeservice.repository;

import com.tkforgeworks.recipeservice.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
}
