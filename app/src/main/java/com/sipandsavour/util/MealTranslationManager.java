package com.sipandsavour.util;

import android.util.Log;

import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.sipandsavour.data.SessionManager;
import com.sipandsavour.data.dto.meal.MealDto;

/**
 * Gestionnaire de traduction pour les recettes TheMealDB
 */
public class MealTranslationManager {

    private static MealTranslationManager instance;
    private final Translator englishFrenchTranslator;
    private boolean isModelDownloaded = false;

    private MealTranslationManager() {
        // Configuration : On traduit de l'Anglais (API) vers le Français (App)
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.FRENCH)
                .build();

        englishFrenchTranslator = Translation.getClient(options);
        downloadModelIfNeeded();
    }

    public static synchronized MealTranslationManager getInstance() {
        if (instance == null) {
            instance = new MealTranslationManager();
        }
        return instance;
    }

    /**
     * Télécharge le modèle de langue silencieusement si ce n'est pas déjà fait.
     */
    private void downloadModelIfNeeded() {
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi() // Télécharge uniquement en Wifi pour économiser les données
                .build();

        englishFrenchTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(v -> {
                    isModelDownloaded = true;
                    Log.d("MealTranslation", "Modèle de traduction pour repas téléchargé avec succès.");
                })
                .addOnFailureListener(e -> Log.e("MealTranslation", "Erreur de téléchargement du modèle", e));
    }

    /**
     * Interface pour le retour asynchrone
     */
    public interface MealTranslationCallback {
        void onTranslationComplete(MealDto translatedMeal);
    }

    /**
     * Traduit une recette complète si l'application est en Français.
     * Sinon, renvoie la recette originale.
     */
    public void translateMealIfNeeded(MealDto meal, MealTranslationCallback callback) {
        if (meal == null) {
            callback.onTranslationComplete(null);
            return;
        }

        // Si l'app est en anglais, on ne traduit rien (l'API est déjà en anglais)
        String currentLang = SessionManager.getInstance().getLanguage();
        if ("en".equals(currentLang)) {
            callback.onTranslationComplete(meal);
            return;
        }

        // Traduction du nom et des instructions
        String originalName = meal.getStrMeal() != null ? meal.getStrMeal() : "";
        String originalInstructions = meal.getStrInstructions() != null ? meal.getStrInstructions() : "";

        // On traduit d'abord le nom
        englishFrenchTranslator.translate(originalName)
                .addOnSuccessListener(translatedName -> {
                    meal.setStrMeal(translatedName);

                    // Ensuite on traduit les instructions
                    englishFrenchTranslator.translate(originalInstructions)
                            .addOnSuccessListener(translatedInstructions -> {
                                meal.setStrInstructions(translatedInstructions);
                                // On renvoie la recette traduite
                                callback.onTranslationComplete(meal);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("MealTranslation", "Erreur de traduction des instructions", e);
                                callback.onTranslationComplete(meal);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("MealTranslation", "Erreur de traduction du nom", e);
                    callback.onTranslationComplete(meal);
                });
    }

    /**
     * Interface pour le retour asynchrone d'une liste
     */
    public interface MealTranslationListCallback {
        void onTranslationComplete(java.util.List<MealDto> translatedMeals);
    }

    /**
     * Traduit une liste de recettes complète.
     */
    public void translateMealListIfNeeded(java.util.List<MealDto> meals, MealTranslationListCallback callback) {
        if (meals == null || meals.isEmpty()) {
            callback.onTranslationComplete(meals);
            return;
        }

        String currentLang = SessionManager.getInstance().getLanguage();
        if ("en".equals(currentLang)) {
            callback.onTranslationComplete(meals);
            return;
        }

        // On utilise un compteur pour savoir quand toutes les recettes de la liste sont traduites
        int[] completedCount = {0};
        for (MealDto meal : meals) {
            translateMealIfNeeded(meal, translatedMeal -> {
                completedCount[0]++;
                if (completedCount[0] == meals.size()) {
                    callback.onTranslationComplete(meals);
                }
            });
        }
    }
}

