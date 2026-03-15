package com.sipandsavour.data.dto;

import java.util.List;

/**
 * DTO représentant un vin.
 */
public class WineDto {

    private int id;
    private String title;
    private String description;
    private String variety;
    private String winery;
    private String province;
    private String country;
    private String color;
    private List<String> keywords;

    // === CONSTRUCTORS ===

    public WineDto() {}

    public WineDto(int id, String title, String description, String variety) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.variety = variety;
    }

    // === GETTERS ===

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getVariety() { return variety; }
    public String getWinery() { return winery; }
    public String getProvince() { return province; }
    public String getCountry() { return country; }
    public String getColor() { return color; }
    public List<String> getKeywords() { return keywords; }

    // === SETTERS ===

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setVariety(String variety) { this.variety = variety; }
    public void setWinery(String winery) { this.winery = winery; }
    public void setProvince(String province) { this.province = province; }
    public void setCountry(String country) { this.country = country; }
    public void setColor(String color) { this.color = color; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }

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

    public String getLocation() {
        StringBuilder sb = new StringBuilder();
        if (province != null && !province.isEmpty()) {
            sb.append(province);
        }
        if (country != null && !country.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(country);
        }
        return sb.toString();
    }
}