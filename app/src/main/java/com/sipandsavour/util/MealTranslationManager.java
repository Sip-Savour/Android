package com.sipandsavour.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.sipandsavour.data.SessionManager;
import com.sipandsavour.data.dto.meal.MealDto;
import com.sipandsavour.util.translation.IngredientDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MealTranslationManager {

    private static final String TAG = "MealTranslation";
    private static MealTranslationManager instance;

    private final Translator translator;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public static synchronized MealTranslationManager getInstance() {
        if (instance == null) {
            instance = new MealTranslationManager();
        }
        return instance;
    }

    private MealTranslationManager() {
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.FRENCH)
                .build();

        this.translator = Translation.getClient(options);
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());

        downloadModel();
    }

    private void downloadModel() {
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(v -> Log.d(TAG, "✅ Modèle ML Kit prêt"))
                .addOnFailureListener(e -> Log.e(TAG, "❌ Erreur téléchargement modèle", e));
    }

    public interface TranslationCallback {
        void onTranslated(MealDto translatedMeal);
    }

    public void translateMeal(MealDto meal, TranslationCallback callback) {
        if (meal == null) {
            mainHandler.post(() -> callback.onTranslated(null));
            return;
        }

        String appLanguage = SessionManager.getInstance().getLanguage();

        if (!appLanguage.equals("fr")) {
            Log.d(TAG, "🌍 App en " + appLanguage + ", pas de traduction");
            mainHandler.post(() -> callback.onTranslated(meal));
            return;
        }

        Log.d(TAG, "🌐 Traduction : " + meal.getName());
        translateWithMLKit(meal, callback);
    }

    private void translateWithMLKit(MealDto meal, TranslationCallback callback) {
        MealDto result = new MealDto();
        result.setId(meal.getId());
        result.setThumbnail(meal.getThumbnail());
        result.setYoutubeUrl(meal.getYoutubeUrl());

        AtomicInteger pending = new AtomicInteger(4); // 4 champs à traduire via ML Kit

        Runnable checkComplete = () -> {
            if (pending.decrementAndGet() == 0) {
                mainHandler.post(() -> callback.onTranslated(result));
                Log.d(TAG, "✅ Traduction complète : " + result.getName());
            }
        };

        // 1. Nom (ML Kit)
        translateText(meal.getName(), translated -> {
            result.setName(translated);
            Log.d(TAG, "   ✓ Nom : " + translated);
            checkComplete.run();
        });

        // 2. Catégorie (ML Kit)
        translateText(meal.getCategory(), translated -> {
            result.setCategory(translated);
            checkComplete.run();
        });

        // 3. Zone (ML Kit)
        translateText(meal.getArea(), translated -> {
            result.setArea(translated);
            checkComplete.run();
        });

        // 4. Instructions (ML Kit)
        translateText(meal.getInstructions(), translated -> {
            result.setInstructions(translated);
            checkComplete.run();
        });

        // 5. Ingrédients (Dictionnaire local - immédiat)
        List<String> ingredients = meal.getIngredients();
        if (ingredients != null && !ingredients.isEmpty()) {
            List<String> translatedIngredients = new ArrayList<>();
            for (String ingredient : ingredients) {
                translatedIngredients.add(IngredientDictionary.translateIngredient(ingredient));
            }
            result.setIngredients(translatedIngredients);
            Log.d(TAG, "   ✓ Ingrédients : " + translatedIngredients.size() + " items (dictionnaire)");
        } else {
            result.setIngredients(new ArrayList<>());
        }

        // 6. Mesures (Dictionnaire local - immédiat)
        List<String> measures = meal.getMeasures();
        if (measures != null && !measures.isEmpty()) {
            List<String> translatedMeasures = new ArrayList<>();
            for (String measure : measures) {
                translatedMeasures.add(IngredientDictionary.translateMeasure(measure));
            }
            result.setMeasures(translatedMeasures);
            Log.d(TAG, "   ✓ Mesures : " + translatedMeasures.size() + " items (dictionnaire)");
        } else {
            result.setMeasures(new ArrayList<>());
        }
    }

    private void translateText(String text, OnTextTranslated callback) {
        if (text == null || text.trim().isEmpty()) {
            callback.onTranslated("");
            return;
        }

        translator.translate(text)
                .addOnSuccessListener(callback::onTranslated)
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Erreur ML Kit : " + e.getMessage());
                    callback.onTranslated(text);
                });
    }

    private interface OnTextTranslated {
        void onTranslated(String text);
    }

    public void translateMealIfNeeded(MealDto meal, TranslationCallback callback) {
        translateMeal(meal, callback);
    }
}