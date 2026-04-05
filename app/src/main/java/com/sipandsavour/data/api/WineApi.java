package com.sipandsavour.data.api;

import com.sipandsavour.data.dto.AddFavoriteRequest;
import com.sipandsavour.data.dto.DailySuggestionResponse;
import com.sipandsavour.data.dto.PredictRequest;
import com.sipandsavour.data.dto.PredictResponse;
import com.sipandsavour.data.dto.WineDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Interface Retrofit pour les endpoints liés aux vins.
 */
public interface WineApi {

    /**
     * Prédit le meilleur vin selon les features et la couleur
     * POST /predict
     * Body: { "features": [feature1, feature2, ...], "color": "red" }
     * Response: { "wine": { ... } }
     */
    @POST("predict")
    Call<PredictResponse> predict(@Body PredictRequest request);

    /**
     * Récupère la suggestion hebdomadaire
     * GET /weekly
     * Response: { "wine": { ... } }
     */
    @GET("wines/weekly")
    Call<DailySuggestionResponse> getWeeklySuggestion();

    /**
     * Récupère les favoris de l'utilisateur
     * GET /favorites
     */
    @GET("favorites")
    Call<List<WineDto>> getFavorites();

    /**
     * Ajoute un vin aux favoris
     * POST /favorites
     * Body: { "wineId": 123 }
     * Response: 200 OK si ajouté avec succès
     */
    @POST("favorites")
    Call<Void> addFavorite(@Body AddFavoriteRequest request);

    /**
     * Supprime un vin des favoris
     * DELETE /favorites/{wineId}
     * Response: 200 OK si supprimé avec succès
     */
    @DELETE("favorites/{wineId}")
    Call<Void> removeFavorite(@Path("wineId") int wineId);

    /**
     * Récupère les détails d'un vin
     * GET /wines/{wineId}
     * Response: { "wine": { ... } }
     */
    @GET("wines/{wineId}")
    Call<WineDto> getWineById(@Path("wineId") int wineId);

    /**
     * Récupère des vins aléatoires
     * GET /wines/random
     * Response: [ { ... }, { ... }, ... ]
     */
    @GET("wines/random")
    Call<List<WineDto>> getRandomWines();
}