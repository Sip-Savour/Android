package com.sipandsavour.data.dto;

/**
 * Requête d'ajout aux favoris.
 */
public class AddFavoriteRequest {

    private int wineId;

    // === CONSTRUCTORS ===

    public AddFavoriteRequest() {}

    public AddFavoriteRequest(int wineId) {
        this.wineId = wineId;
    }

    // === GETTERS ===

    public int getWineId() { return wineId; }

    // === SETTERS ===

    public void setWineId(int wineId) { this.wineId = wineId; }
}