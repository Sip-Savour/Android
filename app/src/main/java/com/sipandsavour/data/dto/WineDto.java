package com.sipandsavour.data.dto;

import java.io.Serializable;

/**
 * DTO représentant un vin.
 */
public class WineDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private String description;
    private String variety;
    private String color;

    // === CONSTRUCTORS ===

    public WineDto() {}

    public WineDto(int id, String title, String description, String variety, String color) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.variety = variety;
        this.color = color;
    }

    // === GETTERS ===

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getVariety() { return variety; }
    public String getColor() { return color; }

    // === SETTERS ===

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setVariety(String variety) { this.variety = variety; }
    public void setColor(String color) { this.color = color; }

    // === HELPERS ===

    public String getColorDisplayName() {
        if (color == null) return "Inconnu";
        switch (color.toLowerCase()) {
            case "red": return "Rouge";
            case "white": return "Blanc";
            case "rose":
            case "rosé": return "Rosé";
            default: return color;
        }
    }
}