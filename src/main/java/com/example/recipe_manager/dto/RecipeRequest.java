package com.example.recipe_manager.dto;

import com.example.recipe_manager.entity.RecipeCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecipeRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    @Pattern(regexp = "(?s).*\\p{L}.*", message = "Title must contain at least one letter")
    private String title;

    @NotNull(message = "Category is required")
    private RecipeCategory category;

    @NotBlank(message = "Ingredients are required")
    @Size(min = 10, max = 3000, message = "Ingredients must be between 10 and 3000 characters")
    @Pattern(
            regexp = "(?s).*\\p{L}.*",
            message = "Ingredients must contain at least one letter"
    )
    private String ingredients;

    @NotBlank(message = "Instructions are required")
    @Size(min = 20, max = 5000, message = "Instructions must be between 20 and 5000 characters")
    @Pattern(
            regexp = "(?s).*\\p{L}.*",
            message = "Instructions must contain at least one letter"
    )
    private String instructions;
}