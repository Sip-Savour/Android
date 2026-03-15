package com.sipandsavour.data.dto;

/**
 * Requête d'inscription.
 */
public class RegisterRequest {

    private String username;
    private String email;
    private String password;
    private String dateOfBirth;

    // === CONSTRUCTORS ===

    public RegisterRequest() {}

    public RegisterRequest(String username, String email, String password, String dateOfBirth) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
    }

    // === GETTERS ===

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getDateOfBirth() { return dateOfBirth; }

    // === SETTERS ===

    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
}