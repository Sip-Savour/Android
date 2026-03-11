package com.sipandsavour.data.dto;

/**
 * Réponse de suggestion quotidienne.
 */
public class DailySuggestionResponse {

    private String mealName;
    private String mealDescription;
    private String ingredients;
    private WineDto wine;

    // === GETTERS ===

    public String getMealName() { return mealName; }
    public String getMealDescription() { return mealDescription; }
    public String getIngredients() { return ingredients; }
    public WineDto getWine() { return wine; }

    // === SETTERS ===

    public void setMealName(String mealName) { this.mealName = mealName; }
    public void setMealDescription(String mealDescription) { this.mealDescription = mealDescription; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }
    public void setWine(WineDto wine) { this.wine = wine; }
}