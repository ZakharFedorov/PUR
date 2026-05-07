package com.example.recipe_manager.controller;

import com.example.recipe_manager.dto.MyRecipesStats;
import com.example.recipe_manager.dto.RecipeRequest;
import com.example.recipe_manager.entity.RecipeCategory;
import com.example.recipe_manager.security.CustomUserDetails;
import com.example.recipe_manager.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MyRecipeController {

    private final RecipeService recipeService;

    @GetMapping("/my-recipes")
    public String myRecipes(
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        var recipes = recipeService.findByAuthor(userDetails.getUser());
        MyRecipesStats stats = recipeService.getStatsByAuthor(userDetails.getUser());

        model.addAttribute("recipes", recipes);
        model.addAttribute("totalRecipes", stats.totalRecipes());
        model.addAttribute("categoryCount", stats.categoryCount());
        model.addAttribute("recipesThisMonth", stats.recipesThisMonth());

        return "my-recipes";
    }

    @GetMapping("/recipes/new")
    public String showCreateForm(Model model) {
        model.addAttribute("recipeRequest", new RecipeRequest());
        model.addAttribute("categories", RecipeCategory.values());
        model.addAttribute("formAction", "/recipes");

        return "recipe-form";
    }

    @PostMapping("/recipes")
    public String createRecipe(
            @Valid @ModelAttribute RecipeRequest recipeRequest,
            BindingResult bindingResult,
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", RecipeCategory.values());
            model.addAttribute("formAction", "/recipes");
            return "recipe-form";
        }

        recipeService.create(recipeRequest, userDetails.getUser());

        return "redirect:/my-recipes";
    }

    @GetMapping("/recipes/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        var recipe = recipeService.findOwnedRecipe(id, userDetails.getUser());

        RecipeRequest request = new RecipeRequest();
        request.setTitle(recipe.getTitle());
        request.setCategory(recipe.getCategory());
        request.setIngredients(recipe.getIngredients());
        request.setInstructions(recipe.getInstructions());

        model.addAttribute("recipeRequest", request);
        model.addAttribute("recipeId", id);
        model.addAttribute("categories", RecipeCategory.values());
        model.addAttribute("formAction", "/recipes/" + id);

        return "recipe-form";
    }

    @PostMapping("/recipes/{id}")
    public String updateRecipe(
            @PathVariable Long id,
            @Valid @ModelAttribute RecipeRequest recipeRequest,
            BindingResult bindingResult,
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("recipeId", id);
            model.addAttribute("categories", RecipeCategory.values());
            model.addAttribute("formAction", "/recipes/" + id);
            return "recipe-form";
        }

        recipeService.update(id, recipeRequest, userDetails.getUser());

        return "redirect:/my-recipes";
    }

    @PostMapping("/recipes/{id}/delete")
    public String deleteRecipe(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        recipeService.delete(id, userDetails.getUser());

        return "redirect:/my-recipes";
    }
}