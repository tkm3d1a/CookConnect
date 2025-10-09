package com.tkforgeworks.recipeservice.model;

import com.tkforgeworks.recipeservice.model.enums.MeasurementValue;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class IngredientListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private IngredientList ingredientList;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeasurementValue measurementValue;

    @ManyToOne(fetch = FetchType.EAGER)
    private Ingredient ingredient;
}
