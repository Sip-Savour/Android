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
     */
    @POST("predict")
    Call<PredictResponse> predict(@Body PredictRequest request);

    /**
     * Récupère la suggestion hebdomadaire
     * GET /weekly
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
     */
    @POST("favorites")
    Call<Void> addFavorite(@Body AddFavoriteRequest request);

    /**
     * Supprime un vin des favoris
     * DELETE /favorites/{wineId}
     */
    @DELETE("favorites/{wineId}")
    Call<Void> removeFavorite(@Path("wineId") int wineId);

    /**
     * Récupère les détails d'un vin
     * GET /wines/{wineId}
     */
    @GET("wines/{wineId}")
    Call<WineDto> getWineById(@Path("wineId") int wineId);
}