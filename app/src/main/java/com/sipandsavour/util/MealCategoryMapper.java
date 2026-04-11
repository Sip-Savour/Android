package com.sipandsavour.util;

/**
 * Utilitaire pour mapper les catégories de nourriture de l'app aux catégories TheMealDB.
 */
public final class MealCategoryMapper {

    /**
     * Mappe une sous-catégorie de l'app à une catégorie MealDB.
     * @param appSubcategory La sous-catégorie de l'app (ex: "Bœuf", "Agneau", "Poisson blanc", "Légumes grillés", "Fromage doux")
     * @return La catégorie MealDB correspondante (ex: "Beef", "Seafood", "Vegetarian", "Miscellaneous") ou null si pas de mapping
     */
    public static String mapToMealDBCategory(String appSubcategory) {
        if (appSubcategory == null) return null;

        switch (appSubcategory.toLowerCase()) {
            case "bœuf":
                return "Beef";
            case "agneau":
                return "Lamb";
            case "porc":
                return "Pork";
            case "volaille":
                return "Chicken";
            case "gibier":
                return "Game"; // Peut ne pas exister, mais essayer
            case "poisson blanc":
            case "poisson gras":
            case "fruits de mer":
            case "crustacés":
                return "Seafood";
            case "légumes grillés":
            case "salade":
            case "pâtes":
            case "risotto":
                return "Vegetarian"; // Ou Side, mais Vegetarian plus large
            case "fromage doux":
            case "fromage affiné":
            case "fromage bleu":
            case "chèvre":
                return "Miscellaneous"; // Pas de catégorie fromage spécifique
            default:
                return null;
        }
    }

    /**
     * Mappe une catégorie principale de l'app à une catégorie MealDB par défaut.
     * @param appCategory La catégorie principale de l'app (ex: "Viande", "Poisson", "Végétarien", "Fromage", "Fruits")
     * @return La catégorie MealDB correspondante (ex: "Beef", "Seafood", "Vegetarian", "Miscellaneous", "Dessert") ou null si pas de mapping
     */
    public static String mapMainCategoryToMealDB(String appCategory) {
        if (appCategory == null) return null;

        switch (appCategory.toLowerCase()) {
            case "viande":
                return "Beef"; // Défaut pour viande
            case "poisson":
                return "Seafood";
            case "végétarien":
                return "Vegetarian";
            case "fromage":
                return "Miscellaneous";
            case "fruits":
                return "Dessert";
            default:
                return null;
        }
    }
}
