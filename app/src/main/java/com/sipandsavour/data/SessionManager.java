package com.sipandsavour.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    public void saveToken(@NonNull String token) {
        prefs.edit().putString(Constants.KEY_TOKEN, token).apply();
    }

    @Nullable
    public String getToken() {
        return prefs.getString(Constants.KEY_TOKEN, null);
    }

    public boolean hasToken() {
        String token = getToken();
        return token != null && !token.isEmpty();
    }

    // =======================================================
    //  USER INFO
    // =======================================================

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

    public void setPreferredColor(@Nullable String color) {
        if (color == null) {
            prefs.edit().remove(Constants.KEY_PREF_COLOR).apply();
        } else {
            prefs.edit().putString(Constants.KEY_PREF_COLOR, color).apply();
        }
    }

    @Nullable
    public String getPreferredColor() {
        return prefs.getString(Constants.KEY_PREF_COLOR, null);
    }

    public void setPreferredFeatures(@Nullable Set<String> features) {
        if (features == null || features.isEmpty()) {
            prefs.edit().remove(Constants.KEY_PREF_FEATURES).apply();
        } else {
            prefs.edit().putStringSet(Constants.KEY_PREF_FEATURES, features).apply();
        }
    }

    @Nullable
    public Set<String> getPreferredFeatures() {
        return prefs.getStringSet(Constants.KEY_PREF_FEATURES, null);
    }

    // =======================================================
    //  HISTORIQUE
    // =======================================================

    public void addWineToHistory(int wineId) {
        String historyStr = prefs.getString(Constants.KEY_HISTORY, "");
        String newId = String.valueOf(wineId);

        java.util.List<String> ids = new java.util.ArrayList<>();
        if (!historyStr.isEmpty()) {
            ids.addAll(java.util.Arrays.asList(historyStr.split(",")));
        }

        ids.removeAll(java.util.Collections.singleton(newId));
        ids.add(0, newId);

        if (ids.size() > 50) {
            ids = ids.subList(0, 50);
        }

        String newHistory = android.text.TextUtils.join(",", ids);
        prefs.edit().putString(Constants.KEY_HISTORY, newHistory).apply();
    }

    public java.util.List<Integer> getHistoryIds() {
        String historyStr = prefs.getString(Constants.KEY_HISTORY, "");
        java.util.List<Integer> ids = new java.util.ArrayList<>();
        if (!historyStr.isEmpty()) {
            for (String id : historyStr.split(",")) {
                try {
                    ids.add(Integer.parseInt(id));
                } catch (NumberFormatException ignored) {}
            }
        }
        return ids;
    }

    // =======================================================
    //  LOGOUT / SESSION
    // =======================================================

    public void logout() {
        prefs.edit().clear().apply();
    }

    public void restoreSession() {
        // Optionnel : Logique de restauration si nécessaire
    }

    // =======================================================
    //  LANGUE / THÈME
    // =======================================================

    public void setLanguage(String langCode) {
        prefs.edit().putString(Constants.KEY_LANGUAGE, langCode).apply();
    }

    /**
     * Récupère la langue (détecte automatiquement si non définie)
     */
    public String getLanguage() {
        String saved = prefs.getString(Constants.KEY_LANGUAGE, null);

        if (saved == null) {
            String systemLang = java.util.Locale.getDefault().getLanguage();
            return (systemLang.equals("fr")) ? "fr" : "en";
        }
        return saved;
    }

    public void setTheme(int themeMode) {
        prefs.edit().putInt("theme_mode", themeMode).apply();
    }

    public int getTheme() {
        return prefs.getInt("theme_mode", androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    // =======================================================
    //  SÉLECTION ALIMENTAIRE
    // =======================================================

    public void setLastSelectedSubcategory(String subcategory) {
        prefs.edit().putString("last_subcategory", subcategory).apply();
    }

    public String getLastSelectedSubcategory() {
        return prefs.getString("last_subcategory", null);
    }
}