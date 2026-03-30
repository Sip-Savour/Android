package com.sipandsavour.data.dto.meal;

import java.util.List;

/**
 * Réponse pour les catégories de repas.
 */
public class MealCategoriesResponse {

    private List<MealCategoryDto> categories;

    // Constructor
    public MealCategoriesResponse() {}

    // Getter
    public List<MealCategoryDto> getCategories() { return categories; }

    // Setter
    public void setCategories(List<MealCategoryDto> categories) { this.categories = categories; }
}
