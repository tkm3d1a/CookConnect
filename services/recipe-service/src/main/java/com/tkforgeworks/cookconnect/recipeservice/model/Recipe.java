package com.tkforgeworks.cookconnect.recipeservice.model;

import com.tkforgeworks.cookconnect.recipeservice.model.enums.SkillLevel;
import com.tkforgeworks.cookconnect.recipeservice.model.enums.VisibilitySettings;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@Table(
        indexes = {
                @Index(name = "idx_recipe_created_by", columnList = "created_by"),
                @Index(name = "idx_recipe_created_at", columnList = "created_at"),
                @Index(name = "idx_recipe_title", columnList = "title"),
                @Index(name = "idx_recipe_created_by_created_at", columnList = "created_by, created_at") // Composite index
        }
)
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    private String description;
    private String createdBy;
    private String createdByUsername;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    @Enumerated(EnumType.STRING)
    private VisibilitySettings recipeVisibilitySettings;
    @Enumerated(EnumType.STRING)
    private SkillLevel skillLevel;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "ingredient_list_id", referencedColumnName = "id")
    private IngredientList ingredientList;
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "instruction_list_id", referencedColumnName = "id")
    private InstructionList instructionList;
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "tag_list_id", referencedColumnName = "id")
    private TagList tagList;

    @OneToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<RecipeNote> recipeNotes = new HashSet<>();
}
