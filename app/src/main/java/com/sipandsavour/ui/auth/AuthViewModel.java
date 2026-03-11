package com.sipandsavour.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel pour les écrans d'authentification.
 */
public class AuthViewModel extends ViewModel {

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
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
        // TODO: Appeler Repository.login(email, password)
        // TODO: Mettre à jour isLoading, errorMessage, loginSuccess

        isLoading.setValue(true);

        // Simulation temporaire
        isLoading.setValue(false);
        loginSuccess.setValue(true);
    }

    public void register(String name, String email, String password, String dob) {
        // TODO: Valider les champs
        // TODO: Appeler Repository.register(name, email, password, dob)
        // TODO: Mettre à jour isLoading, errorMessage, registerSuccess

        isLoading.setValue(true);

        // Simulation temporaire
        isLoading.setValue(false);
        registerSuccess.setValue(true);
    }

    public void clearError() {
        errorMessage.setValue(null);
    }
}