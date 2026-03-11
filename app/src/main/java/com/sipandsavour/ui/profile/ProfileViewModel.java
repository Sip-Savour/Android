package com.sipandsavour.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel pour l'écran de profil.
 */
public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> userName = new MutableLiveData<>();
    private final MutableLiveData<String> userEmail = new MutableLiveData<>();
    private final MutableLiveData<String> userDob = new MutableLiveData<>();

    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<String> getUserEmail() {
        return userEmail;
    }

    public LiveData<String> getUserDob() {
        return userDob;
    }

    public void loadUserData() {
        // TODO: Charger les données depuis SessionManager
        // userName.setValue(SessionManager.getInstance().getUsername());
        // userEmail.setValue(SessionManager.getInstance().getEmail());
    }

    public void logout() {
        // TODO: Appeler SessionManager.logout()
        // TODO: Naviguer vers l'écran de connexion
    }
}