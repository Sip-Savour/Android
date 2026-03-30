package com.sipandsavour.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sipandsavour.data.api.ApiClient;
import com.sipandsavour.util.Constants;

import java.util.Set;

/**
 * Gestionnaire de session utilisateur.
 * Stocke le token, les infos utilisateur et l'état de connexion.
 */
public final class SessionManager {

    private static volatile SessionManager instance;
    private final SharedPreferences prefs;

    private SessionManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(Constants.PREF_SESSION, Context.MODE_PRIVATE);
    }

    /**
     * Initialise le singleton
     */
    public static void init(Context context) {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager(context);
                }
            }
        }
    }

    /**
     * Retourne l'instance singleton
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SessionManager not initialized. Call init() first.");
        }
        return instance;
    }

    // =======================================================
    //  TOKEN
    // =======================================================

    /**
     * Sauvegarde le token d'authentification
     */
    public void saveToken(@NonNull String token) {
        prefs.edit().putString(Constants.KEY_TOKEN, token).apply();
        ApiClient.getInstance().setAuthToken(token);
    }

    /**
     * Retourne le token stocké
     */
    @Nullable
    public String getToken() {
        return prefs.getString(Constants.KEY_TOKEN, null);
    }

    /**
     * Vérifie si un token existe
     */
    public boolean hasToken() {
        String token = getToken();
        return token != null && !token.isEmpty();
    }

    // =======================================================
    //  USER INFO
    // =======================================================

    /**
     * Sauvegarde les infos utilisateur après login/register
     */
    public void saveUser(int userId, @NonNull String username, @NonNull String email) {
        prefs.edit()
                .putInt(Constants.KEY_USER_ID, userId)
                .putString(Constants.KEY_USERNAME, username)
                .putString(Constants.KEY_EMAIL, email)
                .putBoolean(Constants.KEY_LOGGED_IN, true)
                .apply();
    }

    public int getUserId() {
        return prefs.getInt(Constants.KEY_USER_ID, -1);
    }

    @Nullable
    public String getUsername() {
        return prefs.getString(Constants.KEY_USERNAME, null);
    }

    @Nullable
    public String getEmail() {
        return prefs.getString(Constants.KEY_EMAIL, null);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(Constants.KEY_LOGGED_IN, false) && hasToken();
    }

    // =======================================================
    //  PREFERENCES
    // =======================================================

    public void setDarkMode(boolean enabled) {
        prefs.edit().putBoolean(Constants.KEY_DARK_MODE, enabled).apply();
    }

    public boolean isDarkMode() {
        return prefs.getBoolean(Constants.KEY_DARK_MODE, false);
    }

    /**
     * Sauvegarde la couleur de vin préférée de l'utilisateur (ex: "Red", "White", "Rose", ou null)
     */
    public void setPreferredColor(@Nullable String color) {
        if (color == null) {
            prefs.edit().remove(Constants.KEY_PREF_COLOR).apply();
        } else {
            prefs.edit().putString(Constants.KEY_PREF_COLOR, color).apply();
        }
    }

    /**
     * Retourne la couleur préférée, ou null si l'utilisateur n'a rien choisi
     */
    @Nullable
    public String getPreferredColor() {
        return prefs.getString(Constants.KEY_PREF_COLOR, null);
    }

    /**
     * Sauvegarde les features (arômes/caractéristiques) favorites
     */
    public void setPreferredFeatures(@Nullable Set<String> features) {
        if (features == null || features.isEmpty()) {
            prefs.edit().remove(Constants.KEY_PREF_FEATURES).apply();
        } else {
            prefs.edit().putStringSet(Constants.KEY_PREF_FEATURES, features).apply();
        }
    }

    /**
     * Retourne les features favorites, ou null
     */
    @Nullable
    public Set<String> getPreferredFeatures() {
        return prefs.getStringSet(Constants.KEY_PREF_FEATURES, null);
    }

    // =======================================================
    //  HISTORIQUE
    // =======================================================

    /**
     * Ajoute un ID de vin à l'historique (max 50, le plus récent en premier)
     */
    public void addWineToHistory(int wineId) {
        String historyStr = prefs.getString(Constants.KEY_HISTORY, "");
        String newId = String.valueOf(wineId);

        java.util.List<String> ids = new java.util.ArrayList<>();
        if (!historyStr.isEmpty()) {
            ids.addAll(java.util.Arrays.asList(historyStr.split(",")));
        }

        // SÉCURITÉ ABSOLUE : On supprime TOUTES les anciennes occurrences de ce vin
        ids.removeAll(java.util.Collections.singleton(newId));

        // On l'ajoute tout en haut de la liste (index 0)
        ids.add(0, newId);

        // On limite la taille de l'historique à 50
        if (ids.size() > 50) {
            ids = ids.subList(0, 50);
        }

        // On sauvegarde la nouvelle chaîne propre
        String newHistory = android.text.TextUtils.join(",", ids);
        prefs.edit().putString(Constants.KEY_HISTORY, newHistory).apply();
    }

    /**
     * Récupère la liste des IDs de l'historique
     */
    public java.util.List<Integer> getHistoryIds() {
        String historyStr = prefs.getString(Constants.KEY_HISTORY, "");
        java.util.List<Integer> ids = new java.util.ArrayList<>();
        if (!historyStr.isEmpty()) {
            for (String id : historyStr.split(",")) {
                ids.add(Integer.parseInt(id));
            }
        }
        return ids;
    }

    // =======================================================
    //  LOGOUT
    // =======================================================

    /**
     * Déconnecte l'utilisateur et efface toutes les données de session
     */
    public void logout() {
        prefs.edit().clear().apply();
        ApiClient.getInstance().clearAuthToken();
    }

    /**
     * Restaure le token au démarrage de l'app
     * (appeler dans Application.onCreate après ApiClient.init)
     */
    public void restoreSession() {
        String token = getToken();
        if (token != null && !token.isEmpty()) {
            ApiClient.getInstance().setAuthToken(token);
        }
    }

    // =======================================================
    //  LANGUE / LOCALE
    // =======================================================

    /**
     * Sauvegarde la langue choisie (ex: "fr" ou "en")
     */
    public void setLanguage(String langCode) {
        prefs.edit().putString(Constants.KEY_LANGUAGE, langCode).apply();
    }

/**
 * Récupère la langue (détecte automatiquement si non définie)
 */
public String getLanguage() {
    String saved = prefs.getString(Constants.KEY_LANGUAGE, null);

    // Si l'utilisateur n'a jamais choisi de langue, on utilise celle du système
    if (saved == null) {
        String systemLang = java.util.Locale.getDefault().getLanguage();
        // On supporte uniquement fr/en
        return (systemLang.equals("fr")) ? "fr" : "en";
    }

    return saved;
}

    // --- GESTION DU THÈME ---
    public void setTheme(int themeMode) {
        prefs.edit().putInt("theme_mode", themeMode).apply();
    }

    public int getTheme() {
        // Par défaut, on suit le thème du système
        return prefs.getInt("theme_mode", androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    // =======================================================
    //  SÉLECTION ALIMENTAIRE
    // =======================================================

    /**
     * Sauvegarde la dernière sous-catégorie sélectionnée
     */
    public void setLastSelectedSubcategory(String subcategory) {
        prefs.edit().putString("last_subcategory", subcategory).apply();
    }

    /**
     * Récupère la dernière sous-catégorie sélectionnée
     */
    public String getLastSelectedSubcategory() {
        return prefs.getString("last_subcategory", null);
    }
}