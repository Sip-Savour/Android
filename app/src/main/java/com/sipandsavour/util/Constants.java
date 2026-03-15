package com.sipandsavour.util;

public final class Constants {

    private Constants() {}

    // === API ===
    // URL de développement (émulateur Android)
    public static final String BASE_URL_DEV = "http://10.0.2.2:8000/";

    // URL de production (à modifier avec votre serveur)
    public static final String BASE_URL_PROD = "http://10.0.2.2:8000/";

    // URL active
    public static final String BASE_URL = BASE_URL_DEV;

    // === ENDPOINTS ===
    public static final String EP_LOGIN = "auth/login";
    public static final String EP_REGISTER = "auth/register";
    public static final String EP_USER_ME = "user/me";
    public static final String EP_PREDICT = "predict";
    public static final String EP_WEEKLY = "weekly";
    public static final String EP_FAVORITES = "favorites";

    // === TIMEOUTS (seconds) ===
    public static final int TIMEOUT_CONNECT = 15;
    public static final int TIMEOUT_READ = 30;
    public static final int TIMEOUT_WRITE = 15;

    // === CACHE ===
    public static final long CACHE_SIZE = 10 * 1024 * 1024; // 10 MB
    public static final int CACHE_MAX_AGE = 5;   // minutes
    public static final int CACHE_MAX_STALE = 7; // days

    // === PREFERENCES ===
    public static final String PREF_SESSION = "sip_savour_session";
    public static final String PREF_THEME = "sip_savour_theme";
    public static final String KEY_TOKEN = "auth_token";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_LOGGED_IN = "is_logged_in";
    public static final String KEY_DARK_MODE = "dark_mode";

    // === DATABASE ===
    public static final String DB_NAME = "sip_savour_db";
    public static final int DB_VERSION = 1;

    // === HEADERS ===
    public static final String HEADER_AUTH = "Authorization";
    public static final String HEADER_BEARER = "Bearer ";
    public static final String HEADER_CONTENT = "Content-Type";
    public static final String CONTENT_JSON = "application/json";

    // === NOTIFICATION ===
    public static final String CHANNEL_DAILY_ID = "daily_suggestion";
    public static final int NOTIFICATION_DAILY_ID = 1001;

    // === WORKER ===
    public static final String WORKER_DAILY_TAG = "daily_suggestion_worker";
}