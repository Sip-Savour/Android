package com.sipandsavour.data.api;

import android.content.Context;

import com.sipandsavour.util.Constants;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Client Retrofit singleton.
 */
public final class ApiClient {

    private static volatile ApiClient instance;
    private final Retrofit retrofit;
    private String authToken = null;

    /**
     * Constructeur privé.
     * @param context Le contexte de l'application.
     */
    private ApiClient(Context context) {
        // Logging
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Auth interceptor
        Interceptor authInterceptor = chain -> {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder()
                    .header(Constants.HEADER_CONTENT, Constants.CONTENT_JSON);

            if (authToken != null && !authToken.isEmpty()) {
                builder.header(Constants.HEADER_AUTH, Constants.HEADER_BEARER + authToken);
            }

            return chain.proceed(builder.build());
        };

        // Cache
        File cacheDir = new File(context.getCacheDir(), "http_cache");
        Cache cache = new Cache(cacheDir, Constants.CACHE_SIZE);

        // OkHttp
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(Constants.TIMEOUT_CONNECT, TimeUnit.SECONDS)
                .readTimeout(Constants.TIMEOUT_READ, TimeUnit.SECONDS)
                .writeTimeout(Constants.TIMEOUT_WRITE, TimeUnit.SECONDS)
                .cache(cache)
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build();

        // Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Initialise le singleton ApiClient. Doit être appelé avant d'utiliser getInstance().
     * @param context Le contexte de l'application
     */
    public static void init(Context context) {
        if (instance == null) {
            synchronized (ApiClient.class) {
                if (instance == null) {
                    instance = new ApiClient(context.getApplicationContext());
                }
            }
        }
    }

    /**
     * Récupère l'instance singleton de ApiClient. Assurez-vous d'appeler init() avant d'appeler cette méthode.
     * @return L'instance du ApiClient
     * @throws IllegalStateException si init() n'a pas été appelé avant
      */
    public static ApiClient getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ApiClient not initialized. Call init() first.");
        }
        return instance;
    }

    /**
     * Définit le token d'authentification à utiliser pour les requêtes API.
     * @param token Le token d'authentification à utiliser pour les requêtes
      */
    public void setAuthToken(String token) {
        this.authToken = token;
    }

    /**
     * Efface le token d'authentification (ex: lors de la déconnexion)
     * @param token Le token d'authentification à effacer
     */
    public void clearAuthToken() {
        this.authToken = null;
    }
 
    /**
     * Récupère une instance de MealApi pour effectuer des appels liés aux repas.
     * @return Une instance de AuthApi pour les opérations d'authentification
      */
    public AuthApi getAuthApi() {
        return retrofit.create(AuthApi.class);
    }

    /**
     * @return Une instance de WineApi pour les opérations liées aux vins
      */
    public WineApi getWineApi() {
        return retrofit.create(WineApi.class);
    }
}