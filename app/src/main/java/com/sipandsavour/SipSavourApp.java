package com.sipandsavour;

import android.app.Application;
import android.util.Log;

import com.sipandsavour.data.SessionManager;
import com.sipandsavour.data.api.ApiClient;
import com.sipandsavour.util.Constants;

/**
 * Application principale Sip & Savour.
 */
public class SipSavourApp extends Application {

    private static final String TAG = "SipSavourApp";

    @Override
    public void onCreate() {
        super.onCreate();

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
    }
}