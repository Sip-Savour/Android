package com.sipandsavour.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sipandsavour.data.Repository;
import com.sipandsavour.data.dto.AuthResponse;
import com.sipandsavour.ui.common.UiState;

/**
 * ViewModel pour les écrans d'authentification.
 */
public class AuthViewModel extends ViewModel {

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private static final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registerSuccess = new MutableLiveData<>();

    /**
     * Récupère l'état de chargement.
     * @return LiveData contenant l'état de chargement
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Récupère le message d'erreur.
     * @return LiveData contenant le message d'erreur
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Récupère l'état de succès du login.
     * @return LiveData contenant l'état de succès du login
     */
    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    /**
     * Récupère l'état de succès de l'enregistrement.
     * @return LiveData contenant l'état de succès de l'enregistrement
     */
    public LiveData<Boolean> getRegisterSuccess() {
        return registerSuccess;
    }

    /**
     * Effectue la connexion de l'utilisateur.
     * @param email L'adresse email de l'utilisateur
     * @param password Le mot de passe de l'utilisateur
     */
    public void login(String email, String password) {
    
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

    /**
     * Effectue l'enregistrement de l'utilisateur.
     * @param name Le nom de l'utilisateur
     * @param email L'adresse email de l'utilisateur
     * @param password Le mot de passe de l'utilisateur
     * @param dob La date de naissance de l'utilisateur
     */
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

    /**
     * Efface le message d'erreur.
     */
    public void clearError() {
        errorMessage.setValue(null);
    }

    /**
     * Déconnecte l'utilisateur.
     */
    public static void logout() {
        loginSuccess.setValue(false);
    }
}