package com.sipandsavour.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sipandsavour.data.SessionManager;

/**
 * ViewModel pour l'écran de profil.
 */
public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> userName = new MutableLiveData<>();
    private final MutableLiveData<String> userEmail = new MutableLiveData<>();
    private final MutableLiveData<String> userDob = new MutableLiveData<>();

    /**
     * Retourne les données du nom d'utilisateur.
     */
    public LiveData<String> getUserName() {
        return userName;
    }

    /**
     * Retourne les données de l'email de l'utilisateur.
     */
    public LiveData<String> getUserEmail() {
        return userEmail;
    }

    /**
     * Retourne les données de la date de naissance de l'utilisateur.
     */
    public LiveData<String> getUserDob() {
        return userDob;
    }

    /**
     * Charge les données de l'utilisateur.
     */
    public void loadUserData() {
        userName.setValue(SessionManager.getInstance().getUsername());
        userEmail.setValue(SessionManager.getInstance().getEmail());
    }

    /**
     * Effectue la déconnexion de l'utilisateur.
     */
    public void logout() {
        SessionManager.getInstance().logout();
    }
}