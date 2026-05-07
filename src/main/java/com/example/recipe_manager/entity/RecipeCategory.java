package com.example.recipe_manager.entity;

import lombok.Getter;

@Getter
public enum RecipeCategory {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner"),
    DESSERT("Dessert"),
    DRINKS("Drinks"),
    OTHER("Other");

    private final String displayName;

    RecipeCategory(String displayName) {
        this.displayName = displayName;
    }

}