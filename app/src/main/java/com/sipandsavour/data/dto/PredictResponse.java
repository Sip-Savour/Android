package com.sipandsavour.data.dto;

/**
 * Réponse de prédiction de vin.
 */
public class PredictResponse {

    private String cepage;
    private BottleResponse bottle;

    // === CONSTRUCTORS ===

    public PredictResponse() {}

    public PredictResponse(String cepage, BottleResponse bottle) {
        this.cepage = cepage;
        this.bottle = bottle;
    }

    // === GETTERS ===

    public String getCepage() { return cepage; }
    public BottleResponse getBottle() { return bottle; }

    // === SETTERS ===

    public void setCepage(String cepage) { this.cepage = cepage; }
    public void setBottle(BottleResponse bottle) { this.bottle = bottle; }
}