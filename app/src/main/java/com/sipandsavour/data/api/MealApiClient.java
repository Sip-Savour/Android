package com.sipandsavour.data.api;

import android.content.Context;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Client Retrofit pour l'API TheMealDB.
 */
public final class MealApiClient {

    private static volatile MealApiClient instance;
    private final Retrofit retrofit;

    private MealApiClient(Context context) {
        // Logging
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Cache
        File cacheDir = new File(context.getCacheDir(), "meal_http_cache");
        Cache cache = new Cache(cacheDir, 10 * 1024 * 1024); // 10 MB

        // OkHttp
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .cache(cache)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        // Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.themealdb.com/api/json/v1/1/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Initialise le singleton MealApiClient. Doit être appelé avant d'utiliser getInstance().
     * @param context Le contexte de l'application, utilisé pour configurer le cache HTTP.
     */
    public static void init(Context context) {
        if (instance == null) {
            synchronized (MealApiClient.class) {
                if (instance == null) {
                    instance = new MealApiClient(context.getApplicationContext());
                }
            }
        }
    }

    /**
     * Récupère l'instance singleton de MealApiClient. Assurez-vous d'appeler init() avant d'appeler cette méthode.
     * @return L'instance singleton de MealApiClient.
     * @throws IllegalStateException Si init() n'a pas été appelé avant d'appeler cette méthode.
     */
    public static MealApiClient getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MealApiClient not initialized. Call init() first.");
        }
        return instance;
    }

    /**
     * Récupère l'instance de MealApi pour effectuer des appels API.
     * @return L'instance de MealApi créée par Retrofit.
     */
    public MealApi getMealApi() {
        return retrofit.create(MealApi.class);
    }
}
