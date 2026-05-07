package com.example.recipe_manager.exception;

public class RecipeNotFoundException extends RuntimeException {

    public RecipeNotFoundException(Long id) {
        super("Recipe with id " + id + " was not found");
    }
}
