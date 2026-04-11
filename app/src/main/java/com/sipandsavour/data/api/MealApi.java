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
     * @return Une liste de catégories de repas encapsulée dans un MealCategoriesResponse
     */
    @GET("categories.php")
    Call<MealCategoriesResponse> getCategories();

    /**
     * Filtre les repas par catégorie
     * GET /filter.php?c={category}
     * @param category La catégorie de repas à filtrer (ex: "Seafood", "Vegetarian", etc.)
     * @return Une liste de repas correspondant à la catégorie spécifiée encapsulée dans un
     */
    @GET("filter.php")
    Call<MealFilterResponse> filterByCategory(@Query("c") String category);

    /**
     * Recherche un repas par nom
     * GET /search.php?s={name}
     * @param name Le nom du repas à rechercher (ex: "Arrabiata", "Chicken Handi", etc.)
     * @return Une liste de repas correspondant au nom spécifié encapsulée dans un Meal
     */
    @GET("search.php")
    Call<MealFilterResponse> searchMeal(@Query("s") String name);

    /**
     * Récupère un repas aléatoire
     * GET /random.php
     * @return Un repas aléatoire encapsulé dans un MealFilterResponse
     */
    @GET("random.php")
    Call<MealFilterResponse> getRandomMeal();

    /**
     * Récupère les détails complets d'un repas par son ID
     * GET /lookup.php?i={id}
     * @param id L'ID du repas à récupérer (ex: "52772", "52874", etc.)
     * @return Les détails complets du repas correspondant à l'ID spécifié encapsul
     */
    @GET("lookup.php")
    Call<MealFilterResponse> getMealDetails(@Query("i") String id);
}
