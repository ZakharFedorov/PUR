package com.example.recipe_manager.controller;

import com.example.recipe_manager.entity.RecipeCategory;
import com.example.recipe_manager.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping("/")
    public String recipes(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) RecipeCategory category,
            Model model
    ) {
        model.addAttribute("recipes", recipeService.findAllFiltered(q, category));
        model.addAttribute("categories", RecipeCategory.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("searchQuery", q);
        model.addAttribute("recipeOfTheDay", recipeService.findRandomRecipe().orElse(null));

        return "recipes";
    }

    @GetMapping("/recipes/{id}")
    public String recipeDetail(@PathVariable Long id, Model model) {
        var recipe = recipeService.findById(id);

        model.addAttribute("recipe", recipe);
        model.addAttribute("ingredients", splitLines(recipe.getIngredients()));
        model.addAttribute("instructionSteps", splitLines(recipe.getInstructions()));

        return "recipe-detail";
    }

    private List<String> splitLines(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return Arrays.stream(text.split("\\R"))
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .toList();
    }
}