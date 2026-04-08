package com.sipandsavour;

import android.app.Application;
import android.util.Log;

import com.sipandsavour.data.SessionManager;
import com.sipandsavour.data.api.ApiClient;
import com.sipandsavour.data.api.MealApiClient;
import com.sipandsavour.util.Constants;

/**
 * Application principale Sip & Savour.
 * Gère l'initialisation des composants globaux tels que SessionManager, ApiClient et MealApiClient.
 * Assure que ApiClient n'est initialisé que si BASE_URL est définie, et gère les exceptions potentielles lors de l'initialisation.
 */
public class SipSavourApp extends Application {

    private static final String TAG = "SipSavourApp";
    private static SipSavourApp instance;

    /**
     * Appelé lorsque l'application est créée.
     * @param savedInstanceState État sauvegardé de l'application (non utilisé ici).
     */
    @Override
    public void onCreate() {
        super.onCreate();
        
        instance = this;

        // Initialiser SessionManager
        SessionManager.init(this);

        // Initialiser ApiClient seulement si BASE_URL est définie
        if (Constants.BASE_URL != null && !Constants.BASE_URL.isEmpty()) {
            try {
                ApiClient.init(this);
                SessionManager.getInstance().restoreSession();
                Log.d(TAG, "ApiClient initialized with URL: " + Constants.BASE_URL);
            } catch (Exception e) {
                Log.e(TAG, "Failed to initialize ApiClient", e);
            }
        } else {
            Log.w(TAG, "BASE_URL is empty, ApiClient not initialized");
        }

        // Initialiser MealApiClient
        try {
            MealApiClient.init(this);
            Log.d(TAG, "MealApiClient initialized");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize MealApiClient", e);
        }
    }

    /** Fournit une instance de SipSavourApp.
     * @return L'instance de SipSavourApp.
     */
    public static SipSavourApp getInstance() {
        return instance;
    }
}
