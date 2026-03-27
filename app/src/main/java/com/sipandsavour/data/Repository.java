package com.sipandsavour.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sipandsavour.data.api.ApiClient;
import com.sipandsavour.data.api.AuthApi;
import com.sipandsavour.data.api.WineApi;
import com.sipandsavour.data.api.MealApi;
import com.sipandsavour.data.api.MealApiClient;
import com.sipandsavour.data.dto.AuthResponse;
import com.sipandsavour.data.dto.DailySuggestionResponse;
import com.sipandsavour.data.dto.LoginRequest;
import com.sipandsavour.data.dto.PredictRequest;
import com.sipandsavour.data.dto.PredictResponse;
import com.sipandsavour.data.dto.RegisterRequest;
import com.sipandsavour.data.dto.WineDto;
import com.sipandsavour.data.dto.AddFavoriteRequest;
import com.sipandsavour.data.dto.meal.MealFilterResponse;
import com.sipandsavour.ui.common.UiState;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository central pour toutes les opérations data.
 * Abstraction entre les ViewModels et les sources de données (API, cache, DB).
 */
public final class Repository {

    private static volatile Repository instance;

    private static AuthApi authApi = null;
    private final WineApi wineApi;
    private final MealApi mealApi;
    private static SessionManager sessionManager = null;

    private Repository() {
        ApiClient apiClient = ApiClient.getInstance();
        authApi = apiClient.getAuthApi();
        this.wineApi = apiClient.getWineApi();
        MealApiClient mealApiClient = MealApiClient.getInstance();
        this.mealApi = mealApiClient.getMealApi();
        sessionManager = SessionManager.getInstance();
    }

    public static Repository getInstance() {
        if (instance == null) {
            synchronized (Repository.class) {
                if (instance == null) {
                    instance = new Repository();
                }
            }
        }
        return instance;
    }

    // =======================================================
    //  AUTHENTIFICATION
    // =======================================================

    /**
     * Connexion utilisateur
     */
    public static LiveData<UiState<AuthResponse>> login(String email, String password) {
        MutableLiveData<UiState<AuthResponse>> result = new MutableLiveData<>();
        result.setValue(UiState.loading());

        authApi.login(new LoginRequest(email, password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();
                    sessionManager.saveToken(auth.getToken());
                    sessionManager.saveUser(auth.getUserId(), auth.getUsername(), auth.getEmail());
                    result.setValue(UiState.success(auth));
                } else {
                    result.setValue(UiState.error("Email ou mot de passe incorrect"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                result.setValue(UiState.error("Erreur de connexion : " + t.getMessage()));
            }
        });

        return result;
    }

    /**
     * Inscription utilisateur
     */
    public LiveData<UiState<AuthResponse>> register(String username, String email, String password, String dob) {
        MutableLiveData<UiState<AuthResponse>> result = new MutableLiveData<>();
        result.setValue(UiState.loading());

        authApi.register(new RegisterRequest(username, email, password, dob)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();
                    sessionManager.saveToken(auth.getToken());
                    sessionManager.saveUser(auth.getUserId(), auth.getUsername(), auth.getEmail());
                    result.setValue(UiState.success(auth));
                } else {
                    result.setValue(UiState.error("Erreur lors de l'inscription"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                result.setValue(UiState.error("Erreur réseau : " + t.getMessage()));
            }
        });

        return result;
    }

    /**
     * Déconnexion
     */
    public static void logout() {
        sessionManager.logout();
    }

    // =======================================================
    //  PRÉDICTION DE VINS
    // =======================================================

    /**
     * Prédit les meilleurs vins selon les features et la couleur
     */
    public LiveData<UiState<PredictResponse>> predict(String features, String color) {
        MutableLiveData<UiState<PredictResponse>> result = new MutableLiveData<>();
        result.setValue(UiState.loading());

        wineApi.predict(new PredictRequest(features, color)).enqueue(new Callback<PredictResponse>() {
            @Override
            public void onResponse(@NonNull Call<PredictResponse> call, @NonNull Response<PredictResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(UiState.success(response.body()));
                } else {
                    result.setValue(UiState.error("Aucun vin trouvé"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<PredictResponse> call, @NonNull Throwable t) {
                result.setValue(UiState.error("Erreur de prédiction : " + t.getMessage()));
            }
        });

        return result;
    }

    // =======================================================
    //  SUGGESTION HEBDOMADAIRE
    // =======================================================

    public LiveData<UiState<DailySuggestionResponse>> getWeeklySuggestion() {
        MutableLiveData<UiState<DailySuggestionResponse>> result = new MutableLiveData<>();
        result.setValue(UiState.loading());

        wineApi.getWeeklySuggestion().enqueue(new Callback<DailySuggestionResponse>() {
            @Override
            public void onResponse(@NonNull Call<DailySuggestionResponse> call, @NonNull Response<DailySuggestionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(UiState.success(response.body()));
                } else {
                    result.setValue(UiState.error("Pas de suggestion disponible"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<DailySuggestionResponse> call, @NonNull Throwable t) {
                result.setValue(UiState.error("Erreur : " + t.getMessage()));
            }
        });

        return result;
    }

    // =======================================================
    //  FAVORIS
    // =======================================================

    public LiveData<UiState<List<WineDto>>> getFavorites() {
        MutableLiveData<UiState<List<WineDto>>> result = new MutableLiveData<>();
        result.setValue(UiState.loading());

        wineApi.getFavorites().enqueue(new Callback<List<WineDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<WineDto>> call, @NonNull Response<List<WineDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(UiState.success(response.body()));
                } else {
                    result.setValue(UiState.error("Impossible de charger les favoris"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<WineDto>> call, @NonNull Throwable t) {
                result.setValue(UiState.error("Erreur : " + t.getMessage()));
            }
        });

        return result;
    }

    public LiveData<UiState<Boolean>> addFavorite(int wineId) {
        MutableLiveData<UiState<Boolean>> result = new MutableLiveData<>();
        result.setValue(UiState.loading());

        wineApi.addFavorite(new AddFavoriteRequest(wineId)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(UiState.success(true));
                } else {
                    result.setValue(UiState.error("Erreur lors de l'ajout"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                result.setValue(UiState.error("Erreur : " + t.getMessage()));
            }
        });

        return result;
    }

    public LiveData<UiState<Boolean>> removeFavorite(int wineId) {
        MutableLiveData<UiState<Boolean>> result = new MutableLiveData<>();
        result.setValue(UiState.loading());

        wineApi.removeFavorite(wineId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(UiState.success(true));
                } else {
                    result.setValue(UiState.error("Erreur lors de la suppression"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                result.setValue(UiState.error("Erreur : " + t.getMessage()));
            }
        });

        return result;
    }

    // =======================================================
    //  RÉCUPÉRATION D'UN VIN PAR SON ID
    // =======================================================

    public LiveData<UiState<WineDto>> getWineById(int wineId) {
        MutableLiveData<UiState<WineDto>> result = new MutableLiveData<>();
        result.setValue(UiState.loading());

        wineApi.getWineById(wineId).enqueue(new Callback<WineDto>() {
            @Override
            public void onResponse(@NonNull Call<WineDto> call, @NonNull Response<WineDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(UiState.success(response.body()));
                } else {
                    result.setValue(UiState.error("Erreur " + response.code() + " : Impossible de récupérer ce vin"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<WineDto> call, @NonNull Throwable t) {
                result.setValue(UiState.error("Erreur de connexion : " + t.getMessage()));
            }
        });

        return result;
    }

    public LiveData<UiState<WineDto>> getWeeklyRecommendation() {
        MutableLiveData<UiState<WineDto>> finalResult = new MutableLiveData<>();
        finalResult.setValue(UiState.loading());

        // 1. Récupérer les préférences locales
        String myPrefColor = SessionManager.getInstance().getPreferredColor();
        java.util.Set<String> prefFeaturesSet = SessionManager.getInstance().getPreferredFeatures();

        // Formater les features comme attendu par l'API (avec des espaces)
        StringBuilder sb = new StringBuilder();
        if (prefFeaturesSet != null && !prefFeaturesSet.isEmpty()) {
            for (String flavor : prefFeaturesSet) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(flavor.replace("_", " ")); // Nettoyage au cas où
            }
        }
        String myPrefFeatures = sb.toString();

        // 2. Appeler la méthode predict existante
        LiveData<UiState<PredictResponse>> predictSource = predict(myPrefFeatures, myPrefColor);
        predictSource.observeForever(new androidx.lifecycle.Observer<UiState<PredictResponse>>() {
            @Override
            public void onChanged(UiState<PredictResponse> state) {
                if (state.isLoading()) return; // On attend la fin du chargement

                predictSource.removeObserver(this); // Désabonnement

                if (state.isSuccess() && state.getData() != null && state.getData().getBottle() != null && !state.getData().getBottle().isEmpty()) {

                    java.util.List<com.sipandsavour.data.dto.BottleResponse> bottles = state.getData().getBottle();

                    // 3. Tirage au sort hebdomadaire (basé sur l'année et la semaine)
                    java.util.Calendar calendar = java.util.Calendar.getInstance();
                    int year = calendar.get(java.util.Calendar.YEAR);
                    int week = calendar.get(java.util.Calendar.WEEK_OF_YEAR);

                    // En utilisant une "seed" fixe pour la semaine, le random donnera toujours le même index
                    java.util.Random random = new java.util.Random(year * 1000L + week);
                    int randomIndex = random.nextInt(bottles.size());

                    int chosenWineId = bottles.get(randomIndex).getId();

                    // 4. Récupérer les détails complets du vin choisi
                    LiveData<UiState<WineDto>> wineSource = getWineById(chosenWineId);
                    wineSource.observeForever(new androidx.lifecycle.Observer<UiState<WineDto>>() {
                        @Override
                        public void onChanged(UiState<WineDto> wineState) {
                            if (wineState.isLoading()) return;

                            wineSource.removeObserver(this);

                            if (wineState.isSuccess() && wineState.getData() != null) {
                                // Succès total : on renvoie le vin complet à l'interface
                                finalResult.setValue(UiState.success(wineState.getData()));
                            } else {
                                finalResult.setValue(UiState.error("Impossible de récupérer les détails du vin de la semaine."));
                            }
                        }
                    });

                } else {
                    finalResult.setValue(UiState.error("Aucune recommandation trouvée pour vos préférences."));
                }
            }
        });

        return finalResult;
    }

    // =======================================================
    //  MEALS (TheMealDB)
    // =======================================================

    /**
     * Récupère les repas filtrés par catégorie
     */
    public LiveData<UiState<MealFilterResponse>> getMealsByCategory(String category) {
        MutableLiveData<UiState<MealFilterResponse>> result = new MutableLiveData<>();
        result.setValue(UiState.loading());

        mealApi.filterByCategory(category).enqueue(new Callback<MealFilterResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealFilterResponse> call, @NonNull Response<MealFilterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(UiState.success(response.body()));
                } else {
                    result.setValue(UiState.error("Aucun repas trouvé"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<MealFilterResponse> call, @NonNull Throwable t) {
                result.setValue(UiState.error("Erreur : " + t.getMessage()));
            }
        });

        return result;
    }

    /**
     * Recherche un repas par nom
     */
    public LiveData<UiState<MealFilterResponse>> searchMeal(String name) {
        MutableLiveData<UiState<MealFilterResponse>> result = new MutableLiveData<>();
        result.setValue(UiState.loading());

        mealApi.searchMeal(name).enqueue(new Callback<MealFilterResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealFilterResponse> call, @NonNull Response<MealFilterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(UiState.success(response.body()));
                } else {
                    result.setValue(UiState.error("Aucun repas trouvé"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<MealFilterResponse> call, @NonNull Throwable t) {
                result.setValue(UiState.error("Erreur : " + t.getMessage()));
            }
        });

        return result;
    }

    /**
     * Récupère un repas aléatoire
     */
    public LiveData<UiState<MealFilterResponse>> getRandomMeal() {
        MutableLiveData<UiState<MealFilterResponse>> result = new MutableLiveData<>();
        result.setValue(UiState.loading());

        mealApi.getRandomMeal().enqueue(new Callback<MealFilterResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealFilterResponse> call, @NonNull Response<MealFilterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(UiState.success(response.body()));
                } else {
                    result.setValue(UiState.error("Erreur lors de la récupération"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<MealFilterResponse> call, @NonNull Throwable t) {
                result.setValue(UiState.error("Erreur : " + t.getMessage()));
            }
        });

        return result;
    }

    /**
     * Récupère les détails complets d'un repas
     */
    public LiveData<UiState<MealFilterResponse>> getMealDetails(String id) {
        MutableLiveData<UiState<MealFilterResponse>> result = new MutableLiveData<>();
        result.setValue(UiState.loading());

        mealApi.getMealDetails(id).enqueue(new Callback<MealFilterResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealFilterResponse> call, @NonNull Response<MealFilterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(UiState.success(response.body()));
                } else {
                    result.setValue(UiState.error("Détails du repas non trouvés"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<MealFilterResponse> call, @NonNull Throwable t) {
                result.setValue(UiState.error("Erreur : " + t.getMessage()));
            }
        });

        return result;
    }

    // =======================================================
    //  HELPERS
    // =======================================================

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    public String getUsername() {
        return sessionManager.getUsername();
    }

    public String getEmail() {
        return sessionManager.getEmail();
    }
}

