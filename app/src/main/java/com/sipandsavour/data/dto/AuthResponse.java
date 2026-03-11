package com.sipandsavour.data.dto;

/**
 * Réponse d'authentification (login/register).
 */
public class AuthResponse {

    private String token;
    private int userId;
    private String username;
    private String email;

    // === CONSTRUCTORS ===

    public AuthResponse() {}

    public AuthResponse(String token, int userId, String username, String email) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    // === GETTERS ===

    public String getToken() { return token; }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }

    // === SETTERS ===

    public void setToken(String token) { this.token = token; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
}