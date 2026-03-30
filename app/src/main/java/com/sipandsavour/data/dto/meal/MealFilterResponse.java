package com.sipandsavour.data.dto.meal;

import java.util.List;

/**
 * Réponse pour les filtres de repas (par catégorie, recherche, etc.).
 */
public class MealFilterResponse {

    private List<MealDto> meals;

    // Constructor
    public MealFilterResponse() {}

    // Getter
    public List<MealDto> getMeals() { return meals; }

    // Setter
    public void setMeals(List<MealDto> meals) { this.meals = meals; }
}
