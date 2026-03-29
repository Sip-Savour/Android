package com.sipandsavour.util;

import com.sipandsavour.data.dto.WineDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Utilitaire pour accorder les vins avec les plats (MealDB)
 */
public class WineFoodPairingUtil {

    private static final String CAT_BEEF = "Beef";
    private static final String CAT_LAMB = "Lamb";
    private static final String CAT_PORK = "Pork";
    private static final String CAT_CHICKEN = "Chicken";
    private static final String CAT_SEAFOOD = "Seafood";
    private static final String CAT_PASTA = "Pasta";
    private static final String CAT_VEGETARIAN = "Vegetarian";
    private static final String CAT_GOAT = "Goat";

    private static final List<String> BOLD_RED_VARIETIES = Arrays.asList(
            "cabernet", "shiraz", "syrah", "malbec", "tempranillo",
            "nebbiolo", "sangiovese", "zinfandel", "petite sirah"
    );

    private static final List<String> LIGHT_RED_VARIETIES = Arrays.asList(
            "pinot noir", "merlot", "grenache", "gamay", "barbera"
    );

    private static final List<String> DRY_WHITE_VARIETIES = Arrays.asList(
            "chardonnay", "sauvignon blanc", "pinot grigio", "pinot gris",
            "albariño", "vermentino", "grüner veltliner", "chablis"
    );

    /**
     * Détermine les catégories MealDB compatibles avec un vin
     */
    public static List<String> getCompatibleCategories(WineDto wine) {
        if (wine == null) {
            return Arrays.asList(CAT_CHICKEN, CAT_PASTA);
        }

        String color = wine.getColor() != null ? wine.getColor().toLowerCase() : "";
        String variety = wine.getVariety() != null ? wine.getVariety().toLowerCase() : "";
        String description = wine.getDescription() != null ? wine.getDescription().toLowerCase() : "";

        List<String> categories = new ArrayList<>();

        // === VINS ROUGES ===
        if (color.contains("red") || color.contains("rouge")) {
            if (isBoldRed(variety, description)) {
                categories.add(CAT_BEEF);
                categories.add(CAT_LAMB);
                categories.add(CAT_GOAT);
            } else {
                categories.add(CAT_PORK);
                categories.add(CAT_CHICKEN);
                categories.add(CAT_PASTA);
            }
        }
        // === VINS BLANCS ===
        else if (color.contains("white") || color.contains("blanc")) {
            if (isDryWhite(variety, description)) {
                categories.add(CAT_SEAFOOD);
                categories.add(CAT_CHICKEN);
            } else {
                categories.add(CAT_CHICKEN);
                categories.add(CAT_PASTA);
                categories.add(CAT_VEGETARIAN);
            }
        }
        // === VINS ROSÉS ===
        else if (color.contains("rose") || color.contains("rosé")) {
            categories.add(CAT_SEAFOOD);
            categories.add(CAT_CHICKEN);
            categories.add(CAT_PASTA);
            categories.add(CAT_VEGETARIAN);
        }
        // === DÉFAUT ===
        else {
            categories.add(CAT_CHICKEN);
            categories.add(CAT_PASTA);
        }

        return categories;
    }

    /**
     * Choisit UNE catégorie aléatoire compatible (avec seed hebdomadaire)
     */
    public static String getWeeklyCategory(WineDto wine) {
        List<String> categories = getCompatibleCategories(wine);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int year = calendar.get(java.util.Calendar.YEAR);
        int week = calendar.get(java.util.Calendar.WEEK_OF_YEAR);

        Random random = new Random(year * 1000L + week + 1);
        int index = random.nextInt(categories.size());

        return categories.get(index);
    }

    private static boolean isBoldRed(String variety, String description) {
        for (String boldVariety : BOLD_RED_VARIETIES) {
            if (variety.contains(boldVariety)) {
                return true;
            }
        }

        return description.contains("bold") ||
               description.contains("full-bodied") ||
               description.contains("full body") ||
               description.contains("tannin") ||
               description.contains("oak") ||
               description.contains("dark fruit") ||
               description.contains("black fruit") ||
               description.contains("powerful") ||
               description.contains("robust");
    }

    private static boolean isDryWhite(String variety, String description) {
        for (String dryVariety : DRY_WHITE_VARIETIES) {
            if (variety.contains(dryVariety)) {
                return true;
            }
        }

        return description.contains("dry") ||
               description.contains("crisp") ||
               description.contains("mineral") ||
               description.contains("citrus") ||
               description.contains("acidity") ||
               description.contains("lean");
    }
}