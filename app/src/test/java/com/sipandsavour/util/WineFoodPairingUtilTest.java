package com.sipandsavour.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.sipandsavour.data.dto.WineDto;

import org.junit.Test;

import java.util.List;

public class WineFoodPairingUtilTest {

    @Test
    public void boldRedWine_shouldPairWithBeefAndLamb() {
        WineDto wine = new WineDto(1, "Cabernet Sauvignon", 
                "Full-bodied with dark fruit and tannins", 
                "Cabernet Sauvignon", "Red");

        List<String> categories = WineFoodPairingUtil.getCompatibleCategories(wine);

        assertTrue(categories.contains("Beef"));
        assertTrue(categories.contains("Lamb"));
        assertTrue(categories.contains("Goat"));
    }

    @Test
    public void lightRedWine_shouldPairWithPorkAndChicken() {
        WineDto wine = new WineDto(2, "Pinot Noir", 
                "Light and elegant", 
                "Pinot Noir", "Red");

        List<String> categories = WineFoodPairingUtil.getCompatibleCategories(wine);

        assertTrue(categories.contains("Pork"));
        assertTrue(categories.contains("Chicken"));
        assertTrue(categories.contains("Pasta"));
    }

    @Test
    public void dryWhiteWine_shouldPairWithSeafood() {
        WineDto wine = new WineDto(3, "Sauvignon Blanc", 
                "Crisp and mineral with citrus notes", 
                "Sauvignon Blanc", "White");

        List<String> categories = WineFoodPairingUtil.getCompatibleCategories(wine);

        assertTrue(categories.contains("Seafood"));
        assertTrue(categories.contains("Chicken"));
    }

    @Test
    public void roseWine_shouldPairWithVersatileDishes() {
        WineDto wine = new WineDto(4, "Rosé de Provence", 
                "Fresh and fruity", 
                "Grenache", "Rosé");

        List<String> categories = WineFoodPairingUtil.getCompatibleCategories(wine);

        assertTrue(categories.contains("Seafood"));
        assertTrue(categories.contains("Chicken"));
        assertTrue(categories.contains("Pasta"));
        assertTrue(categories.contains("Vegetarian"));
    }

    @Test
    public void nullWine_shouldReturnDefaultCategories() {
        List<String> categories = WineFoodPairingUtil.getCompatibleCategories(null);

        assertEquals(2, categories.size());
        assertTrue(categories.contains("Chicken"));
        assertTrue(categories.contains("Pasta"));
    }

    @Test
    public void getWeeklyCategory_shouldReturnConsistentResult() {
        WineDto wine = new WineDto(5, "Merlot", "Smooth", "Merlot", "Red");

        String category1 = WineFoodPairingUtil.getWeeklyCategory(wine);
        String category2 = WineFoodPairingUtil.getWeeklyCategory(wine);

        // Doit retourner la même catégorie pour la même semaine
        assertEquals(category1, category2);
        assertNotNull(category1);
    }

    @Test
    public void unknownColorWine_shouldUseDefaultPairing() {
        WineDto wine = new WineDto(6, "Orange Wine", 
                "Unique and complex", 
                "Skin Contact", "Orange");

        List<String> categories = WineFoodPairingUtil.getCompatibleCategories(wine);

        assertTrue(categories.contains("Chicken"));
        assertTrue(categories.contains("Pasta"));
    }
}
