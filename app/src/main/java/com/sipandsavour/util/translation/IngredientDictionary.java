package com.sipandsavour.util.translation;

import java.util.HashMap;
import java.util.Map;

/**
 * Dictionnaire de traduction pour les ingrédients et mesures courants
 */
public class IngredientDictionary {

    private static final Map<String, String> INGREDIENTS = new HashMap<>();
    private static final Map<String, String> MEASURES = new HashMap<>();

    static {
        // === INGRÉDIENTS ===
        // Viandes
        INGREDIENTS.put("beef", "bœuf");
        INGREDIENTS.put("beef brisket", "poitrine de bœuf");
        INGREDIENTS.put("ground beef", "bœuf haché");
        INGREDIENTS.put("minced beef", "bœuf haché");
        INGREDIENTS.put("steak", "steak");
        INGREDIENTS.put("chicken", "poulet");
        INGREDIENTS.put("chicken breast", "blanc de poulet");
        INGREDIENTS.put("pork", "porc");
        INGREDIENTS.put("lamb", "agneau");
        INGREDIENTS.put("bacon", "bacon");
        INGREDIENTS.put("ham", "jambon");
        INGREDIENTS.put("sausage", "saucisse");
        INGREDIENTS.put("meatballs", "boulettes de viande");

        // Poissons & Fruits de mer
        INGREDIENTS.put("fish", "poisson");
        INGREDIENTS.put("salmon", "saumon");
        INGREDIENTS.put("tuna", "thon");
        INGREDIENTS.put("cod", "cabillaud");
        INGREDIENTS.put("shrimp", "crevettes");
        INGREDIENTS.put("prawns", "crevettes");

        // Légumes
        INGREDIENTS.put("onion", "oignon");
        INGREDIENTS.put("onions", "oignons");
        INGREDIENTS.put("garlic", "ail");
        INGREDIENTS.put("garlic clove", "gousse d'ail");
        INGREDIENTS.put("garlic cloves", "gousses d'ail");
        INGREDIENTS.put("tomato", "tomate");
        INGREDIENTS.put("tomatoes", "tomates");
        INGREDIENTS.put("tomato puree", "purée de tomates");
        INGREDIENTS.put("tomato paste", "concentré de tomates");
        INGREDIENTS.put("potato", "pomme de terre");
        INGREDIENTS.put("potatoes", "pommes de terre");
        INGREDIENTS.put("carrot", "carotte");
        INGREDIENTS.put("carrots", "carottes");
        INGREDIENTS.put("celery", "céleri");
        INGREDIENTS.put("pepper", "poivron");
        INGREDIENTS.put("bell pepper", "poivron");
        INGREDIENTS.put("mushroom", "champignon");
        INGREDIENTS.put("mushrooms", "champignons");
        INGREDIENTS.put("spinach", "épinards");
        INGREDIENTS.put("lettuce", "laitue");
        INGREDIENTS.put("cabbage", "chou");
        INGREDIENTS.put("broccoli", "brocoli");
        INGREDIENTS.put("zucchini", "courgette");
        INGREDIENTS.put("eggplant", "aubergine");
        INGREDIENTS.put("cucumber", "concombre");
        INGREDIENTS.put("peas", "petits pois");
        INGREDIENTS.put("corn", "maïs");
        INGREDIENTS.put("beans", "haricots");
        INGREDIENTS.put("green beans", "haricots verts");
        INGREDIENTS.put("swede", "rutabaga");
        INGREDIENTS.put("turnip", "navet");
        INGREDIENTS.put("leek", "poireau");
        INGREDIENTS.put("leeks", "poireaux");
        INGREDIENTS.put("asparagus", "asperges");
        INGREDIENTS.put("plum tomatoes", "tomates Roma");

        // Herbes & Épices
        INGREDIENTS.put("parsley", "persil");
        INGREDIENTS.put("basil", "basilic");
        INGREDIENTS.put("thyme", "thym");
        INGREDIENTS.put("rosemary", "romarin");
        INGREDIENTS.put("oregano", "origan");
        INGREDIENTS.put("cilantro", "coriandre");
        INGREDIENTS.put("coriander", "coriandre");
        INGREDIENTS.put("mint", "menthe");
        INGREDIENTS.put("dill", "aneth");
        INGREDIENTS.put("bay leaf", "feuille de laurier");
        INGREDIENTS.put("bay leaves", "feuilles de laurier");
        INGREDIENTS.put("cumin", "cumin");
        INGREDIENTS.put("paprika", "paprika");
        INGREDIENTS.put("curry", "curry");
        INGREDIENTS.put("cinnamon", "cannelle");
        INGREDIENTS.put("nutmeg", "noix de muscade");
        INGREDIENTS.put("ginger", "gingembre");
        INGREDIENTS.put("turmeric", "curcuma");
        INGREDIENTS.put("chili", "piment");
        INGREDIENTS.put("black pepper", "poivre noir");
        INGREDIENTS.put("ras el hanout", "ras el hanout");

        // Produits de base
        INGREDIENTS.put("salt", "sel");
        INGREDIENTS.put("pepper", "poivre");
        INGREDIENTS.put("sugar", "sucre");
        INGREDIENTS.put("flour", "farine");
        INGREDIENTS.put("bread", "pain");
        INGREDIENTS.put("breadcrumbs", "chapelure");
        INGREDIENTS.put("rice", "riz");
        INGREDIENTS.put("pasta", "pâtes");
        INGREDIENTS.put("noodles", "nouilles");

        // Produits laitiers
        INGREDIENTS.put("milk", "lait");
        INGREDIENTS.put("cream", "crème");
        INGREDIENTS.put("heavy cream", "crème fraîche");
        INGREDIENTS.put("sour cream", "crème aigre");
        INGREDIENTS.put("butter", "beurre");
        INGREDIENTS.put("cheese", "fromage");
        INGREDIENTS.put("parmesan", "parmesan");
        INGREDIENTS.put("mozzarella", "mozzarella");
        INGREDIENTS.put("cheddar", "cheddar");
        INGREDIENTS.put("egg", "œuf");
        INGREDIENTS.put("eggs", "œufs");
        INGREDIENTS.put("egg yolk", "jaune d'œuf");
        INGREDIENTS.put("egg yolks", "jaunes d'œufs");
        INGREDIENTS.put("egg white", "blanc d'œuf");
        INGREDIENTS.put("egg whites", "blancs d'œufs");
        INGREDIENTS.put("yogurt", "yaourt");

        // Huiles & Sauces
        INGREDIENTS.put("oil", "huile");
        INGREDIENTS.put("olive oil", "huile d'olive");
        INGREDIENTS.put("vegetable oil", "huile végétale");
        INGREDIENTS.put("sesame oil", "huile de sésame");
        INGREDIENTS.put("vinegar", "vinaigre");
        INGREDIENTS.put("soy sauce", "sauce soja");
        INGREDIENTS.put("worcestershire sauce", "sauce Worcestershire");
        INGREDIENTS.put("mustard", "moutarde");
        INGREDIENTS.put("ketchup", "ketchup");
        INGREDIENTS.put("mayonnaise", "mayonnaise");

        // Liquides
        INGREDIENTS.put("water", "eau");
        INGREDIENTS.put("stock", "bouillon");
        INGREDIENTS.put("beef stock", "bouillon de bœuf");
        INGREDIENTS.put("chicken stock", "bouillon de poulet");
        INGREDIENTS.put("broth", "bouillon");
        INGREDIENTS.put("wine", "vin");
        INGREDIENTS.put("red wine", "vin rouge");
        INGREDIENTS.put("white wine", "vin blanc");

        // Fruits
        INGREDIENTS.put("lemon", "citron");
        INGREDIENTS.put("lemon juice", "jus de citron");
        INGREDIENTS.put("lime", "citron vert");
        INGREDIENTS.put("orange", "orange");
        INGREDIENTS.put("apple", "pomme");

        // === MESURES ===
        MEASURES.put("cup", "tasse");
        MEASURES.put("cups", "tasses");
        MEASURES.put("tablespoon", "cuillère à soupe");
        MEASURES.put("tablespoons", "cuillères à soupe");
        MEASURES.put("tbsp", "c. à soupe");
        MEASURES.put("tbs", "c. à soupe");
        MEASURES.put("teaspoon", "cuillère à café");
        MEASURES.put("teaspoons", "cuillères à café");
        MEASURES.put("tsp", "c. à café");
        MEASURES.put("ounce", "once");
        MEASURES.put("ounces", "onces");
        MEASURES.put("oz", "oz");
        MEASURES.put("pound", "livre");
        MEASURES.put("pounds", "livres");
        MEASURES.put("lb", "lb");
        MEASURES.put("lbs", "lb");
        MEASURES.put("gram", "gramme");
        MEASURES.put("grams", "grammes");
        MEASURES.put("g", "g");
        MEASURES.put("kilogram", "kilogramme");
        MEASURES.put("kg", "kg");
        MEASURES.put("liter", "litre");
        MEASURES.put("liters", "litres");
        MEASURES.put("ml", "ml");
        MEASURES.put("pinch", "pincée");
        MEASURES.put("clove", "gousse");
        MEASURES.put("cloves", "gousses");
        MEASURES.put("slice", "tranche");
        MEASURES.put("slices", "tranches");
        MEASURES.put("piece", "morceau");
        MEASURES.put("pieces", "morceaux");
        MEASURES.put("handful", "poignée");
        MEASURES.put("bunch", "botte");
        MEASURES.put("large", "gros");
        MEASURES.put("medium", "moyen");
        MEASURES.put("small", "petit");
        MEASURES.put("to taste", "au goût");
        MEASURES.put("crushed", "écrasé");
        MEASURES.put("chopped", "haché");
        MEASURES.put("minced", "émincé");
        MEASURES.put("diced", "en dés");
        MEASURES.put("sliced", "tranché");
    }

    /**
     * Traduit un ingrédient
     */
    public static String translateIngredient(String text) {
        if (text == null || text.trim().isEmpty()) return text;

        String lower = text.toLowerCase().trim();

        // Recherche exacte
        if (INGREDIENTS.containsKey(lower)) {
            return capitalizeFirst(INGREDIENTS.get(lower));
        }

        // Recherche partielle (pour les variantes)
        for (Map.Entry<String, String> entry : INGREDIENTS.entrySet()) {
            if (lower.contains(entry.getKey())) {
                return capitalizeFirst(lower.replace(entry.getKey(), entry.getValue()));
            }
        }

        return text; // Retourne l'original si pas trouvé
    }

    /**
     * Traduit une mesure
     */
    public static String translateMeasure(String text) {
        if (text == null || text.trim().isEmpty()) return text;

        String lower = text.toLowerCase().trim();
        String result = text;

        // Remplacer tous les termes trouvés
        for (Map.Entry<String, String> entry : MEASURES.entrySet()) {
            if (lower.contains(entry.getKey())) {
                result = result.replaceAll("(?i)" + entry.getKey(), entry.getValue());
            }
        }

        return result;
    }

    private static String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}