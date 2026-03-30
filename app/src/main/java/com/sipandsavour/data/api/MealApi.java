package com.sipandsavour.data.api;

import com.sipandsavour.data.dto.meal.MealCategoriesResponse;
import com.sipandsavour.data.dto.meal.MealFilterResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface Retrofit pour l'API TheMealDB.
 */
public interface MealApi {

    /**
     * Récupère toutes les catégories de repas
     * GET /categories.php
     */
    @GET("categories.php")
    Call<MealCategoriesResponse> getCategories();

    /**
     * Filtre les repas par catégorie
     * GET /filter.php?c={category}
     */
    @GET("filter.php")
    Call<MealFilterResponse> filterByCategory(@Query("c") String category);

    /**
     * Recherche un repas par nom
     * GET /search.php?s={name}
     */
    @GET("search.php")
    Call<MealFilterResponse> searchMeal(@Query("s") String name);

    /**
     * Récupère un repas aléatoire
     * GET /random.php
     */
    @GET("random.php")
    Call<MealFilterResponse> getRandomMeal();

    /**
     * Récupère les détails complets d'un repas par son ID
     * GET /lookup.php?i={id}
     */
    @GET("lookup.php")
    Call<MealFilterResponse> getMealDetails(@Query("i") String id);
}
