package com.sipandsavour.ui.auth;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sipandsavour.data.Repository;
import com.sipandsavour.data.dto.AuthResponse;
import com.sipandsavour.ui.common.UiState;
import android.util.Log;

/**
 * ViewModel pour les écrans d'authentification.
 */
public class AuthViewModel extends ViewModel {

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private static final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registerSuccess = new MutableLiveData<>();

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    public LiveData<Boolean> getRegisterSuccess() {
        return registerSuccess;
    }

    public void login(String email, String password) {
        // TODO: Valider les champs
        // TODO: Mettre à jour isLoading, errorMessage, loginSuccess

        isLoading.setValue(true);
        Repository rep = Repository.getInstance();
        LiveData<UiState<AuthResponse>> source = Repository.login(email, password);

        source.observeForever(new Observer<UiState<AuthResponse>>() {
            @Override
            public void onChanged(UiState<AuthResponse> state) {
                if (!state.isLoading()) {
                    // Once it's Success or Error, stop listening!
                    source.removeObserver(this);

                    if (state.isSuccess()) {
                        // Navigate or update UI
                        isLoading.setValue(false);
                        loginSuccess.setValue(true);
                    }
                    if (state.isError()) {
                        isLoading.setValue(false);
                        loginSuccess.setValue(false);
                    }
                }
            }
        });

    }

    public void register(String name, String email, String password, String dob) {
        isLoading.setValue(true);
        clearError();

        // On appelle le vrai Repository
        LiveData<UiState<AuthResponse>> source = Repository.getInstance().register(name, email, password, dob);

        source.observeForever(new Observer<UiState<AuthResponse>>() {
            @Override
            public void onChanged(UiState<AuthResponse> state) {
                if (!state.isLoading()) {
                    source.removeObserver(this); // Désabonnement pour éviter les fuites
                    isLoading.setValue(false);

                    if (state.isSuccess()) {
                        registerSuccess.setValue(true);
                    } else if (state.isError()) {
                        errorMessage.setValue(state.getMessage());
                        registerSuccess.setValue(false);
                    }
                }
            }
        });
    }

    public void clearError() {
        errorMessage.setValue(null);
    }

    public static void logout() {
        loginSuccess.setValue(false);
    }
}