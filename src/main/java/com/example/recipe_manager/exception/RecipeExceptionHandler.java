package com.example.recipe_manager.exception;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(annotations = Controller.class)
public class RecipeExceptionHandler {

    @ExceptionHandler({
            RecipeNotFoundException.class,
            RecipeAccessDeniedException.class
    })
    public String handleRecipeError(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Recipe was not found or you do not have access.");
        return "redirect:/";
    }
}
