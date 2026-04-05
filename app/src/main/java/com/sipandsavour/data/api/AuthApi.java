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
     * @param request LoginRequest contenant email et mot de passe
     * @return Call<AuthResponse> contenant le token JWT et les infos utilisateur
     */
    @POST("/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    /**
     * Inscription utilisateur
     * POST /auth/register
     * @param request RegisterRequest contenant nom, email et mot de passe
     * @return Call<AuthResponse> contenant le token JWT et les infos utilisateur
     */
    @POST("/signup")
    Call<AuthResponse> register(@Body RegisterRequest request);

    /**
     * Récupère les infos de l'utilisateur connecté
     * GET /user/me
     * @return Call<AuthResponse> contenant les infos utilisateur
     */
    @GET("user/me")
    Call<AuthResponse> getCurrentUser();
}