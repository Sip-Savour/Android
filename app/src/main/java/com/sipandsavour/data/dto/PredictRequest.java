package com.sipandsavour.data.dto;

/**
 * Requête de prédiction de vin.
 */
public class PredictRequest {

    private String features;
    private String color;

    // === CONSTRUCTORS ===

    public PredictRequest() {}

    public PredictRequest(String features, String color) {
        this.features = features;
        this.color = color;
    }

    // === GETTERS ===

    public String getFeatures() { return features; }
    public String getColor() { return color; }

    // === SETTERS ===

    public void setFeatures(String features) { this.features = features; }
    public void setColor(String color) { this.color = color; }
}