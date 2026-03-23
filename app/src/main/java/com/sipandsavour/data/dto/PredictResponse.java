package com.sipandsavour.data.dto;

import java.util.List;

/**
 * Réponse de prédiction de vin.
 */
public class PredictResponse {

    // On utilise une List car l'API renvoie plusieurs bouteilles
    private List<BottleResponse> bottle;

    // === CONSTRUCTORS ===
    public PredictResponse() {}

    public PredictResponse(List<BottleResponse> bottle) {
        this.bottle = bottle;
    }

    // === GETTERS ===
    public List<BottleResponse> getBottle() {
        return bottle;
    }

    // === SETTERS ===
    public void setBottle(List<BottleResponse> bottle) {
        this.bottle = bottle;
    }
}