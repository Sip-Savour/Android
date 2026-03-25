package com.sipandsavour.data.api;

import com.sipandsavour.data.dto.AuthResponse;
import com.sipandsavour.data.dto.LoginRequest;
import com.sipandsavour.data.dto.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Interface Retrofit pour les endpoints d'authentification.
 */
public interface AuthApi {

    /**
     * Connexion utilisateur
     * POST /auth/login
     */
    @POST("/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    /**
     * Inscription utilisateur
     * POST /auth/register
     */
    @POST("/signup")
    Call<AuthResponse> register(@Body RegisterRequest request);

    /**
     * Récupère les infos de l'utilisateur connecté
     * GET /user/me
     */
    @GET("user/me")
    Call<AuthResponse> getCurrentUser();
}