package com.example.recipe_manager.dto;

public record MyRecipesStats(
        long totalRecipes,
        long categoryCount,
        long recipesThisMonth
) {
}