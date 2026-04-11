package com.sipandsavour.util;

import android.util.Log;

import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.sipandsavour.data.SessionManager;
import com.sipandsavour.data.dto.WineDto;

public class TranslationManager {

    private static TranslationManager instance;
    private final Translator englishFrenchTranslator;
    private boolean isModelDownloaded = false;

    private TranslationManager() {
        // Configuration : On traduit de l'Anglais (API) vers le Français (App)
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.FRENCH)
                .build();

        englishFrenchTranslator = Translation.getClient(options);
        downloadModelIfNeeded();
    }

    /**
     * Retourne l'instance singleton de TranslationManager.
     * @return L'instance singleton.
     */
    public static synchronized TranslationManager getInstance() {
        if (instance == null) {
            instance = new TranslationManager();
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
                    Log.d("Translation", "Modèle de traduction téléchargé avec succès.");
                })
                .addOnFailureListener(e -> Log.e("Translation", "Erreur de téléchargement du modèle", e));
    }

    /**
     * Interface pour le retour asynchrone
     */
    public interface TranslationCallback {
        void onTranslationComplete(WineDto translatedWine);
    }

    /**
     * Traduit un vin complet si l'application est en Français.
     * Sinon, renvoie le vin original.
     * @param wine Le vin à traduire.
     * @param callback Le callback à appeler une fois la traduction terminée.
     */
    public void translateWineIfNeeded(WineDto wine, TranslationCallback callback) {
        if (wine == null) {
            callback.onTranslationComplete(null);
            return;
        }

        // Si l'app est en anglais, on ne traduit rien (l'API est déjà en anglais)
        String currentLang = SessionManager.getInstance().getLanguage();
        if ("en".equals(currentLang)) {
            callback.onTranslationComplete(wine);
            return;
        }

        // Traduction séquentielle : Description -> Cépage -> Titre (optionnel)
        String originalDescription = wine.getDescription() != null ? wine.getDescription() : "";
        String originalVariety = wine.getVariety() != null ? wine.getVariety() : "";

        englishFrenchTranslator.translate(originalDescription)
                .addOnSuccessListener(translatedDesc -> {
                    wine.setDescription(translatedDesc);

                    englishFrenchTranslator.translate(originalVariety)
                            .addOnSuccessListener(translatedVariety -> {
                                wine.setVariety(translatedVariety);
                                // On renvoie le vin traduit au ViewModel !
                                callback.onTranslationComplete(wine);
                            })
                            .addOnFailureListener(e -> callback.onTranslationComplete(wine)); // En cas d'erreur, on garde l'original

                })
                .addOnFailureListener(e -> {
                    Log.e("Translation", "Erreur de traduction", e);
                    callback.onTranslationComplete(wine); // Si erreur, on renvoie la version anglaise
                });
    }

    /**
     * Interface pour le retour asynchrone d'une liste
     * @param translatedWines La liste de vins traduits une fois la traduction terminée.
     */
    public interface TranslationListCallback {
        void onTranslationComplete(java.util.List<WineDto> translatedWines);
    }

    /**
     * Traduit une liste de vins complète.
     * @param wines La liste de vins à traduire.
     * @param callback Le callback à appeler une fois la traduction de tous les vins terminée
     */
    public void translateWineListIfNeeded(java.util.List<WineDto> wines, TranslationListCallback callback) {
        if (wines == null || wines.isEmpty()) {
            callback.onTranslationComplete(wines);
            return;
        }

        String currentLang = SessionManager.getInstance().getLanguage();
        if ("en".equals(currentLang)) {
            callback.onTranslationComplete(wines);
            return;
        }

        // On utilise un compteur pour savoir quand tous les vins de la liste sont traduits
        int[] completedCount = {0};
        for (WineDto wine : wines) {
            translateWineIfNeeded(wine, translatedWine -> {
                completedCount[0]++;
                if (completedCount[0] == wines.size()) {
                    callback.onTranslationComplete(wines);
                }
            });
        }
    }
}