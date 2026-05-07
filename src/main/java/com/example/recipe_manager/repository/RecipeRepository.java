package com.example.recipe_manager.repository;

import com.example.recipe_manager.entity.Recipe;
import com.example.recipe_manager.entity.RecipeCategory;
import com.example.recipe_manager.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findAllByOrderByCreatedAtDesc();

    List<Recipe> findByAuthorOrderByCreatedAtDesc(User author);

    long countByAuthor(User author);

    long countByAuthorAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            User author,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("SELECT COUNT(DISTINCT r.category) FROM Recipe r WHERE r.author = :author")
    long countDistinctCategoriesByAuthor(User author);

    List<Recipe> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title);

    List<Recipe> findByCategoryOrderByCreatedAtDesc(RecipeCategory category);

    List<Recipe> findByTitleContainingIgnoreCaseAndCategoryOrderByCreatedAtDesc(
            String title,
            RecipeCategory category
    );

    @Query("SELECT r FROM Recipe r ORDER BY function('random')")
    List<Recipe> findRandomRecipe(Pageable pageable);
}