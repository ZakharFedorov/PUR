package com.example.recipe_manager.exception;

public class RecipeAccessDeniedException extends RuntimeException {

    public RecipeAccessDeniedException(Long recipeId) {
        super("You are not allowed to modify recipe with id " + recipeId);
    }
}
