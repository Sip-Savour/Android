package com.sipandsavour.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

/**
 * Service de traduction utilisant ML Kit (Google)
 * Fonctionne offline après téléchargement du modèle
 */
public class LibreTranslateService {
    private static final String TAG = "MLKitTranslate";
    private static LibreTranslateService instance;

    private final Translator translator;
    private final Handler mainHandler;

    public static synchronized LibreTranslateService getInstance() {
        if (instance == null) {
            instance = new LibreTranslateService();
        }
        return instance;
    }

    private LibreTranslateService() {
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.FRENCH)
                .build();

        this.translator = Translation.getClient(options);
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public interface TranslationCallback {
        void onSuccess(String translatedText);
        void onError(String error);
    }

    public void translate(String text, TranslationCallback callback) {
        translate(text, "en", "fr", callback);
    }

    public void translate(String text, String sourceLang, String targetLang, TranslationCallback callback) {
        if (text == null || text.trim().isEmpty()) {
            mainHandler.post(() -> callback.onSuccess(text));
            return;
        }

        translator.translate(text)
                .addOnSuccessListener(translated -> {
                    Log.d(TAG, "✅ Traduit");
                    mainHandler.post(() -> callback.onSuccess(translated));
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Erreur : " + e.getMessage());
                    mainHandler.post(() -> callback.onSuccess(text)); // Retourne l'original
                });
    }

    public void translateBatch(String[] texts, String sourceLang, String targetLang, BatchCallback callback) {
        if (texts == null || texts.length == 0) {
            mainHandler.post(() -> callback.onSuccess(new String[0]));
            return;
        }

        String[] results = new String[texts.length];
        final int[] completed = {0};

        for (int i = 0; i < texts.length; i++) {
            final int index = i;
            translate(texts[i], sourceLang, targetLang, new TranslationCallback() {
                @Override
                public void onSuccess(String translated) {
                    results[index] = translated;
                    completed[0]++;
                    if (completed[0] == texts.length) {
                        mainHandler.post(() -> callback.onSuccess(results));
                    }
                }

                @Override
                public void onError(String error) {
                    results[index] = texts[index];
                    completed[0]++;
                    if (completed[0] == texts.length) {
                        mainHandler.post(() -> callback.onSuccess(results));
                    }
                }
            });
        }
    }

    public interface BatchCallback {
        void onSuccess(String[] translatedTexts);
    }
}