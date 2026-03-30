package com.sipandsavour.data.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WineDtoTest {

    @Test
    public void constructor_setsAllFields() {
        WineDto wine = new WineDto(1, "Château Margaux", 
                "Exceptional Bordeaux", 
                "Cabernet Sauvignon", "Red");

        assertEquals(1, wine.getId());
        assertEquals("Château Margaux", wine.getTitle());
        assertEquals("Exceptional Bordeaux", wine.getDescription());
        assertEquals("Cabernet Sauvignon", wine.getVariety());
        assertEquals("Red", wine.getColor());
    }

    @Test
    public void getColorDisplayName_returnsRouge_forRed() {
        WineDto wine = new WineDto();
        wine.setColor("red");
        assertEquals("Rouge", wine.getColorDisplayName());

        wine.setColor("Red");
        assertEquals("Rouge", wine.getColorDisplayName());

        wine.setColor("RED");
        assertEquals("Rouge", wine.getColorDisplayName());
    }

    @Test
    public void getColorDisplayName_returnsBlanc_forWhite() {
        WineDto wine = new WineDto();
        wine.setColor("white");
        assertEquals("Blanc", wine.getColorDisplayName());
    }

    @Test
    public void getColorDisplayName_returnsRose_forRose() {
        WineDto wine = new WineDto();
        wine.setColor("rose");
        assertEquals("Rosé", wine.getColorDisplayName());

        wine.setColor("rosé");
        assertEquals("Rosé", wine.getColorDisplayName());
    }

    @Test
    public void getColorDisplayName_returnsInconnu_forNull() {
        WineDto wine = new WineDto();
        wine.setColor(null);
        assertEquals("Inconnu", wine.getColorDisplayName());
    }

    @Test
    public void getColorDisplayName_returnsOriginal_forUnknown() {
        WineDto wine = new WineDto();
        wine.setColor("Orange");
        assertEquals("Orange", wine.getColorDisplayName());
    }

    @Test
    public void serializable_shouldWork() {
        WineDto wine = new WineDto(2, "Test", "Desc", "Variety", "Red");
        
        // Vérifie que la classe implémente Serializable
        assertTrue(wine instanceof java.io.Serializable);
    }

    @Test
    public void setters_shouldUpdateFields() {
        WineDto wine = new WineDto();
        
        wine.setId(10);
        wine.setTitle("Nouveau Titre");
        wine.setDescription("Nouvelle Description");
        wine.setVariety("Merlot");
        wine.setColor("White");

        assertEquals(10, wine.getId());
        assertEquals("Nouveau Titre", wine.getTitle());
        assertEquals("Nouvelle Description", wine.getDescription());
        assertEquals("Merlot", wine.getVariety());
        assertEquals("White", wine.getColor());
    }
}
