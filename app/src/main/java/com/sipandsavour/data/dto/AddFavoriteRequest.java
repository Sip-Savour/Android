package com.sipandsavour.data.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Requête d'ajout aux favoris.
 */
public class AddFavoriteRequest {

    // Le serveur s'attend exactement à voir "wineId"
    @SerializedName("wineId")
    private int wineId;

    // Et très probablement "userId" au lieu de "user_id"
    @SerializedName("userId")
    private int userId;

    public AddFavoriteRequest() {}

    public AddFavoriteRequest(int wineId, int userId) {
        this.wineId = wineId;
        this.userId = userId;
    }

    public int getWineId() { return wineId; }
    public void setWineId(int wineId) { this.wineId = wineId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}