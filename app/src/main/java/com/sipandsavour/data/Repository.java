package com.sipandsavour.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sipandsavour.data.api.ApiClient;
import com.sipandsavour.data.api.AuthApi;
import com.sipandsavour.data.api.WineApi;
import com.sipandsavour.data.dto.AuthResponse;
import com.sipandsavour.data.dto.DailySuggestionResponse;
import com.sipandsavour.data.dto.LoginRequest;
import com.sipandsavour.data.dto.PredictRequest;
import com.sipandsavour.data.dto.PredictResponse;
import com.sipandsavour.data.dto.RegisterRequest;
import com.sipandsavour.data.dto.WineDto;
import com.sipandsavour.data.dto.AddFavoriteRequest;
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
    private static SessionManager sessionManager = null;

    private Repository() {
        ApiClient apiClient = ApiClient.getInstance();
        authApi = apiClient.getAuthApi();
        this.wineApi = apiClient.getWineApi();
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