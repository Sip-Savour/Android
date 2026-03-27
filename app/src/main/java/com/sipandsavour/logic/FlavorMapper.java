package com.sipandsavour.logic;

import com.sipandsavour.data.SessionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Logique métier de mapping plat → descripteurs vin.
 *
 * Contient les KEYWORD_GROUPS (groupes de mots-clefs vin)
 * et la logique de conversion des saveurs utilisateur
 * vers une features string compatible avec l'API /predict.
 *
 * Les catégories sont organisées en groupes pour l'UI
 * (accordéons dans l'écran de sélection).
 */
public final class FlavorMapper {

    private FlavorMapper() {}

    // =======================================================
    //  KEYWORD GROUPS — Descripteurs de vin
    // =======================================================

    private static final Map<String, List<String>> KEYWORD_GROUPS = new LinkedHashMap<>();

    static {
        // --- FRUITS ---
        KEYWORD_GROUPS.put("red_fruit",    Arrays.asList("red", "cherry", "raspberry", "strawberry", "cranberry", "pomegranate", "currant", "rhubarb", "watermelon", "sour cherry"));
        KEYWORD_GROUPS.put("black_fruit",  Arrays.asList("black", "blackberry", "cassis", "plum", "dark fruit", "blueberry", "bramble", "boysenberry", "black cherry", "blackcurrant"));
        KEYWORD_GROUPS.put("dried_fruit",  Arrays.asList("raisin", "prune", "fig", "date", "dried fruit", "cooked fruit", "candied", "jammy"));
        KEYWORD_GROUPS.put("citrus",       Arrays.asList("citrus", "lemon", "lime", "grapefruit", "orange", "mandarin", "tangerine", "zest", "rind", "yuzu", "bergamot"));
        KEYWORD_GROUPS.put("tropical",     Arrays.asList("tropical", "pineapple", "melon", "mango", "papaya", "passion", "lychee", "guava", "banana", "kiwi"));
        KEYWORD_GROUPS.put("tree_fruit",   Arrays.asList("apple", "pear", "peach", "apricot", "nectarine", "quince", "yellow fruit"));
        KEYWORD_GROUPS.put("gooseberry",   Arrays.asList("gooseberry"));

        // --- SUCRE & STYLE ---
        KEYWORD_GROUPS.put("dry",          Arrays.asList("dry", "bone dry"));
        KEYWORD_GROUPS.put("sweet",        Arrays.asList("sweet", "sugar", "honey", "lush", "syrup", "botrytis", "late harvest", "dessert", "off-dry", "maple"));

        // --- STRUCTURE ---
        KEYWORD_GROUPS.put("acidity",      Arrays.asList("acid", "acidity", "tart", "crisp", "bright", "lively", "fresh", "freshness", "zesty", "sour", "racy", "zippy"));
        KEYWORD_GROUPS.put("tannins",      Arrays.asList("tannin", "tannins", "tannic", "firm", "chewy", "astringent", "grip", "structured", "muscular"));
        KEYWORD_GROUPS.put("body_full",    Arrays.asList("bodied", "full", "heavy", "dense", "thick", "rich", "richness", "concentrated", "big", "fat", "oily", "viscous", "lush", "opulent"));
        KEYWORD_GROUPS.put("body_light",   Arrays.asList("light", "elegant", "delicate", "thin", "airy", "lean"));
        KEYWORD_GROUPS.put("texture_soft", Arrays.asList("smooth", "soft", "silky", "velvety", "creamy", "round", "supple", "polished", "plush", "seamless"));

        // --- BOISÉ, FUMÉ & ÉPICES ---
        KEYWORD_GROUPS.put("oak",           Arrays.asList("oak", "wood", "cedar", "barrel", "cask", "vanilla", "coconut", "woody", "sandalwood", "pine", "resin"));
        KEYWORD_GROUPS.put("smoke_tobacco", Arrays.asList("smoke", "smoky", "ash", "charcoal", "tobacco", "cigar", "burnt", "charred", "roasted", "campfire", "incense"));
        KEYWORD_GROUPS.put("pastry",        Arrays.asList("brioche", "dough", "yeast", "biscuit", "bread", "toast", "toasty", "butter", "cream", "butterscotch", "caramel", "toffee", "marzipan", "nougat"));
        KEYWORD_GROUPS.put("spices",        Arrays.asList("spice", "spicy", "pepper", "peppery", "cinnamon", "clove", "nutmeg", "licorice", "anise", "cardamom", "ginger"));
        KEYWORD_GROUPS.put("nutty",         Arrays.asList("nutty", "almond", "hazelnut", "walnut", "pecan", "chestnut"));
        KEYWORD_GROUPS.put("cocoa",         Arrays.asList("chocolate", "cocoa", "mocha", "coffee", "espresso", "dark chocolate"));

        // --- VÉGÉTAL & HERBACÉ ---
        KEYWORD_GROUPS.put("herbal",        Arrays.asList("herb", "herbal", "green", "grass", "grassy", "leafy", "stem", "vegetal", "hay", "straw"));
        KEYWORD_GROUPS.put("aromatic_herb", Arrays.asList("mint", "eucalyptus", "menthol", "sage", "thyme", "fennel", "dill", "rosemary", "lavender", "bay leaf", "basil"));
        KEYWORD_GROUPS.put("vegetable",     Arrays.asList("bell pepper", "jalapeno", "capsicum", "olive", "green olive", "black olive", "tomato leaf", "asparagus"));
        KEYWORD_GROUPS.put("floral",        Arrays.asList("floral", "flower", "blossom", "rose", "violet", "jasmine", "honeysuckle", "acacia", "chamomile", "white flower"));

        // --- TERROIR, MINÉRAL ---
        KEYWORD_GROUPS.put("earth",     Arrays.asList("earth", "earthy", "dirt", "soil", "dusty", "mushroom", "truffle", "forest floor", "underbrush", "compost"));
        KEYWORD_GROUPS.put("mineral",   Arrays.asList("mineral", "minerality", "stone", "slate", "flint", "chalk", "chalky", "saline", "salty", "crushed rock", "limestone", "wet stone"));
        KEYWORD_GROUPS.put("savory",    Arrays.asList("savory", "meaty", "bacon", "game", "leather", "animal", "cured meat", "blood", "iron", "umami"));

        // --- QUALITÉ ---
        KEYWORD_GROUPS.put("complex",     Arrays.asList("complex", "complexity", "layered", "nuanced", "depth", "intricate"));
        KEYWORD_GROUPS.put("finish_long", Arrays.asList("long finish", "length", "lingering", "persistent", "endless"));
    }

    // =======================================================
    //  CATÉGORIES UI — Groupes d'accordéon
    // =======================================================

    /**
     * Retourne les 5 groupes de l'UI accordéon.
     * Gère dynamiquement la traduction selon le SessionManager.
     */
    public static List<AccordionCategory> getAccordionCategories() {
        List<AccordionCategory> categories = new ArrayList<>();

        categories.add(new AccordionCategory(
                "category_fruits",
                Arrays.asList("red_fruit", "black_fruit", "dried_fruit", "citrus", "tropical", "tree_fruit")
        ));

        categories.add(new AccordionCategory(
                "category_structure",
                Arrays.asList("acidity", "tannins", "body_full", "body_light", "texture_soft", "dry", "sweet")
        ));

        categories.add(new AccordionCategory(
                "category_wood",
                Arrays.asList("oak", "smoke_tobacco", "pastry", "spices", "nutty", "cocoa")
        ));

        categories.add(new AccordionCategory(
                "category_vegetal",
                Arrays.asList("herbal", "aromatic_herb", "vegetable", "floral")
        ));

        categories.add(new AccordionCategory(
                "category_terroir",
                Arrays.asList("earth", "mineral", "savory", "complex", "finish_long")
        ));

        categories.add(new AccordionCategory(
                "category_color",
                Arrays.asList("Rouge", "Blanc", "Rosé")
        ));

        return categories;
    }

    // =======================================================
    //  MAPPING PLAT → DESCRIPTEURS VIN
    // =======================================================

    /**
     * Convertit un descripteur de saveur utilisateur (ex: "grillé")
     * en mots-clefs vin pertinents. (Supporte le FR et EN)
     */
    public static List<String> mapFlavorToWineKeywords(String flavor) {
        if (flavor == null) return Collections.emptyList();

        String f = flavor.toLowerCase().trim();
        List<String> keywords = new ArrayList<>();

        switch (f) {
            case "grillé":
            case "grilled":
                keywords.addAll(Arrays.asList("smoke", "char", "pepper", "roasted", "tannin"));
                break;
            case "fumé":
            case "smoked":
                keywords.addAll(Arrays.asList("smoke", "smoky", "tobacco", "ash", "charcoal"));
                break;
            case "épicé":
            case "spicy":
                keywords.addAll(Arrays.asList("spice", "pepper", "peppery", "clove", "cinnamon", "ginger"));
                break;
            case "crémeux":
            case "creamy":
                keywords.addAll(Arrays.asList("cream", "butter", "smooth", "soft", "vanilla", "rich"));
                break;
            case "herbacé":
            case "herbal":
                keywords.addAll(Arrays.asList("herb", "herbal", "green", "thyme", "rosemary", "basil"));
                break;
            case "acide":
            case "acidic":
                keywords.addAll(Arrays.asList("acid", "crisp", "tart", "bright", "fresh", "citrus"));
                break;
            case "sucré":
            case "sweet":
                keywords.addAll(Arrays.asList("sweet", "honey", "sugar", "caramel", "vanilla"));
                break;
            case "riche":
            case "rich":
                keywords.addAll(Arrays.asList("rich", "full", "concentrated", "dense", "opulent"));
                break;
            case "léger":
            case "light":
                keywords.addAll(Arrays.asList("light", "delicate", "elegant", "crisp", "fresh"));
                break;
            case "beurré":
            case "buttery":
                keywords.addAll(Arrays.asList("butter", "cream", "toast", "brioche", "vanilla", "oak"));
                break;
            case "rôti":
            case "roasted":
                keywords.addAll(Arrays.asList("roasted", "toast", "smoke", "earth", "oak", "warm"));
                break;
            case "mijoté":
            case "braised":
                keywords.addAll(Arrays.asList("rich", "earth", "mushroom", "savory", "soft", "complex"));
                break;
            case "caramélisé":
            case "caramelized":
                keywords.addAll(Arrays.asList("caramel", "toffee", "butterscotch", "sweet", "rich", "vanilla"));
                break;
            case "poivré":
            case "peppery":
                keywords.addAll(Arrays.asList("pepper", "peppery", "spice", "black", "tannin"));
                break;
            default:
                // Mot inconnu : on le passe tel quel
                keywords.add(f);
                break;
        }

        return keywords;
    }

    /**
     * Convertit une liste de saveurs sélectionnées
     * en features string pour l'API.
     */
    public static String buildFeaturesFromSelections(List<String> selectedFlavors) {
        Set<String> allKeywords = new LinkedHashSet<>();

        if (selectedFlavors != null) {
            for (String flavor : selectedFlavors) {
                allKeywords.addAll(mapFlavorToWineKeywords(flavor));
            }
        }

        return String.join(" ", allKeywords);
    }

    /**
     * Convertit un texte libre décrivant un plat
     * en features string pour l'API.
     */
    public static String buildFeaturesFromText(String freeText) {
        if (freeText == null || freeText.trim().isEmpty()) return "";

        Set<String> allKeywords = new LinkedHashSet<>();
        String[] words = freeText.toLowerCase().trim().split("[\\s,;.]+");

        for (String word : words) {
            List<String> mapped = mapFlavorToWineKeywords(word);
            allKeywords.addAll(mapped);
        }

        return String.join(" ", allKeywords);
    }

    /**
     * Retourne tous les mots-clefs d'un groupe donné.
     */
    public static List<String> getKeywordsForGroup(String groupName) {
        List<String> list = KEYWORD_GROUPS.get(groupName);
        return list != null ? new ArrayList<>(list) : Collections.emptyList();
    }

    /**
     * Retourne les premiers mots-clefs d'un groupe
     * (pour affichage de preview dans les icônes).
     */
    public static String getGroupPreview(String groupName) {
        List<String> list = KEYWORD_GROUPS.get(groupName);
        if (list == null || list.isEmpty()) return groupName;
        return list.get(0);
    }

    /**
     * Nom d'affichage pour un sous-groupe (Géré dynamiquement pour la traduction).
     */
    public static String getGroupDisplayName(String groupKey) {
        boolean isEn = "en".equals(SessionManager.getInstance().getLanguage());
        switch (groupKey) {
            case "red_fruit":      return isEn ? "Red fruits" : "Fruits rouges";
            case "black_fruit":    return isEn ? "Black fruits" : "Fruits noirs";
            case "dried_fruit":    return isEn ? "Dried fruits" : "Fruits secs";
            case "citrus":         return isEn ? "Citrus" : "Agrumes";
            case "tropical":       return isEn ? "Tropical" : "Tropical";
            case "tree_fruit":     return isEn ? "Apple/Pear" : "Pomme/Poire";
            case "acidity":        return isEn ? "Acidity" : "Acidité";
            case "tannins":        return isEn ? "Tannins" : "Tanins";
            case "body_full":      return isEn ? "Full body" : "Corps plein";
            case "body_light":     return isEn ? "Light body" : "Corps léger";
            case "texture_soft":   return isEn ? "Silky" : "Soyeux";
            case "dry":            return isEn ? "Dry" : "Sec";
            case "sweet":          return isEn ? "Sweet" : "Sucré";
            case "oak":            return isEn ? "Oak" : "Boisé";
            case "smoke_tobacco":  return isEn ? "Smoky" : "Fumé";
            case "pastry":         return isEn ? "Pastry/Bakery" : "Pâtissier";
            case "spices":         return isEn ? "Spices" : "Épices";
            case "nutty":          return isEn ? "Nutty" : "Noisette";
            case "cocoa":          return isEn ? "Cocoa" : "Cacao";
            case "herbal":         return isEn ? "Herbal" : "Herbacé";
            case "aromatic_herb":  return isEn ? "Aromatic herbs" : "Herbes arom.";
            case "vegetable":      return isEn ? "Vegetal" : "Végétal";
            case "floral":         return isEn ? "Floral" : "Floral";
            case "earth":          return isEn ? "Earthy" : "Terreux";
            case "mineral":        return isEn ? "Mineral" : "Minéral";
            case "savory":         return isEn ? "Umami" : "Umami";
            case "complex":        return isEn ? "Complex" : "Complexe";
            case "finish_long":    return isEn ? "Long finish" : "Long en bouche";
            case "Rouge":          return isEn ? "Red" : "Rouge";
            case "Blanc":          return isEn ? "White" : "Blanc";
            case "Rosé":           return isEn ? "Rosé" : "Rosé";
            default:               return groupKey;
        }
    }

    // =======================================================
    //  CLASSE INTERNE — Catégorie d'accordéon
    // =======================================================

    public static class AccordionCategory {

        private final String titleKey;
        private final List<String> subGroupKeys;
        private boolean expanded;

        public AccordionCategory(String titleKey, List<String> subGroupKeys) {
            this.titleKey = titleKey;
            this.subGroupKeys = subGroupKeys;
            this.expanded = false;
        }

        public String getTitle()               { return getTitleTranslated(); }
        public String getTitleTranslated() {
            // Retourne le titre traduit dynamiquement selon la langue actuelle
            boolean isEn = "en".equals(SessionManager.getInstance().getLanguage());
            switch (titleKey) {
                case "category_fruits": return "Fruits";
                case "category_structure": return isEn ? "Structure & Body" : "Structure & Corps";
                case "category_wood": return isEn ? "Wood, Smoke & Spices" : "Bois, Fumée & Épices";
                case "category_vegetal": return isEn ? "Vegetal & Floral" : "Végétal & Floral";
                case "category_terroir": return isEn ? "Terroir & Complexity" : "Terroir & Complexité";
                case "category_color": return isEn ? "Color" : "Couleur";
                default: return titleKey;
            }
        }
        public List<String> getSubGroupKeys()  { return subGroupKeys; }
        public boolean isExpanded()             { return expanded; }
        public void setExpanded(boolean expanded) { this.expanded = expanded; }
        public void toggleExpanded()            { this.expanded = !expanded; }

        /** Retourne les noms d'affichage des sous-groupes */
        public List<String> getSubGroupDisplayNames() {
            List<String> names = new ArrayList<>();
            for (String key : subGroupKeys) {
                names.add(FlavorMapper.getGroupDisplayName(key));
            }
            return names;
        }
    }
}