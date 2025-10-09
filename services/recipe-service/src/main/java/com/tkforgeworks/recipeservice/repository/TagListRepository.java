package com.tkforgeworks.recipeservice.repository;

import com.tkforgeworks.recipeservice.model.TagList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagListRepository extends JpaRepository<TagList, Long> {
}
