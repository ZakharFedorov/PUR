package com.example.recipe_manager.service;

import com.example.recipe_manager.dto.MyRecipesStats;
import com.example.recipe_manager.dto.RecipeRequest;
import com.example.recipe_manager.entity.Recipe;
import com.example.recipe_manager.entity.RecipeCategory;
import com.example.recipe_manager.entity.User;
import com.example.recipe_manager.exception.RecipeAccessDeniedException;
import com.example.recipe_manager.exception.RecipeNotFoundException;
import com.example.recipe_manager.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public List<Recipe> findByAuthor(User author) {
        return recipeRepository.findByAuthorOrderByCreatedAtDesc(author);
    }

    public MyRecipesStats getStatsByAuthor(User author) {
        YearMonth currentMonth = YearMonth.now();

        LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime nextMonthStart = currentMonth.plusMonths(1).atDay(1).atStartOfDay();

        long totalRecipes = recipeRepository.countByAuthor(author);
        long categoryCount = recipeRepository.countDistinctCategoriesByAuthor(author);
        long recipesThisMonth = recipeRepository.countByAuthorAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                author,
                monthStart,
                nextMonthStart
        );

        return new MyRecipesStats(
                totalRecipes,
                categoryCount,
                recipesThisMonth
        );
    }

    public Recipe create(RecipeRequest request, User author) {
        Recipe recipe = Recipe.builder()
                .title(normalize(request.getTitle()))
                .category(request.getCategory())
                .ingredients(normalize(request.getIngredients()))
                .instructions(normalize(request.getInstructions()))
                .author(author)
                .build();

        return recipeRepository.save(recipe);
    }

    public Recipe findById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException(id));
    }

    public Recipe findOwnedRecipe(Long id, User user) {
        Recipe recipe = findById(id);
        validateRecipeOwner(recipe, user);

        return recipe;
    }

    public void update(Long id, RecipeRequest request, User user) {
        Recipe recipe = findOwnedRecipe(id, user);

        recipe.setTitle(normalize(request.getTitle()));
        recipe.setCategory(request.getCategory());
        recipe.setIngredients(normalize(request.getIngredients()));
        recipe.setInstructions(normalize(request.getInstructions()));

        recipeRepository.save(recipe);
    }

    public void delete(Long id, User user) {
        Recipe recipe = findOwnedRecipe(id, user);

        recipeRepository.delete(recipe);
    }

    public List<Recipe> findAllFiltered(String query, RecipeCategory category) {
        boolean hasQuery = query != null && !query.isBlank();
        boolean hasCategory = category != null;

        if (hasQuery && hasCategory) {
            return recipeRepository.findByTitleContainingIgnoreCaseAndCategoryOrderByCreatedAtDesc(
                    query.trim(),
                    category
            );
        }

        if (hasQuery) {
            return recipeRepository.findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(query.trim());
        }

        if (hasCategory) {
            return recipeRepository.findByCategoryOrderByCreatedAtDesc(category);
        }

        return recipeRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Recipe> findRandomRecipe() {
        return recipeRepository.findRandomRecipe(PageRequest.of(0, 1))
                .stream()
                .findFirst();
    }

    private void validateRecipeOwner(Recipe recipe, User user) {
        if (!recipe.getAuthor().getId().equals(user.getId())) {
            throw new RecipeAccessDeniedException(recipe.getId());
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
