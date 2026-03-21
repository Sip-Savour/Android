package com.sipandsavour.data.dto;

/**
 * Représente une bouteille dans la réponse de prédiction.
 */
public class BottleResponse {

    private String title;
    private String description;
    private String variety;
    private String color; // <-- NOUVELLE VARIABLE

    // === CONSTRUCTORS ===

    public BottleResponse() {}

    public BottleResponse(String title, String description, String variety, String color) {
        this.title = title;
        this.description = description;
        this.variety = variety;
        this.color = color;
    }

    // === GETTERS ===

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getVariety() { return variety; }
    public String getColor() { return color; }

    // === SETTERS ===

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setVariety(String variety) { this.variety = variety; }
    public void setColor(String color) { this.color = color; } // <-- NOUVEAU SETTER
}