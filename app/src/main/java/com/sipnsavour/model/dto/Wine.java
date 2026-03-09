package com.sipnsavour.model.dto;

import java.util.List;

public class Wine {
    private String id;
    private String cepage;
    private String description;
    private String type;
    private List<String> tags;
    private boolean isFavorite;

    public Wine(String cepage, String description, String type) {
        this.cepage = cepage;
        this.description = description;
        this.type = type;
        this.isFavorite = false;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCepage() {
        return cepage;
    }

    public void setCepage(String cepage) {
        this.cepage = cepage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
