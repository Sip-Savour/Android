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
        userName.setValue(SessionManager.getInstance().getUsername());
        userEmail.setValue(SessionManager.getInstance().getEmail());
    }

    public void logout() {
        // C'est ICI que se passe la vraie déconnexion sans faire planter l'application !
        SessionManager.getInstance().logout();
    }
}