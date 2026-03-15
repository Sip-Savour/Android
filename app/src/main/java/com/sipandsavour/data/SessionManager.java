package com.sipandsavour.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sipandsavour.data.api.ApiClient;
import com.sipandsavour.util.Constants;

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
}