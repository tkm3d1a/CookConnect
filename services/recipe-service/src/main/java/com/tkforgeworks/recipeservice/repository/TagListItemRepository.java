package com.tkforgeworks.recipeservice.repository;

import com.tkforgeworks.recipeservice.model.TagListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagListItemRepository extends JpaRepository<TagListItem, Long> {
}
