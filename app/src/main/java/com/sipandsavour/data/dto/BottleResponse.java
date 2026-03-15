package com.sipandsavour.data.dto;

/**
 * Représente une bouteille dans la réponse de prédiction.
 */
public class BottleResponse {

    private String title;
    private String description;
    private String variety;

    // === CONSTRUCTORS ===

    public BottleResponse() {}

    public BottleResponse(String title, String description, String variety) {
        this.title = title;
        this.description = description;
        this.variety = variety;
    }

    // === GETTERS ===

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getVariety() { return variety; }

    // === SETTERS ===

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setVariety(String variety) { this.variety = variety; }
}