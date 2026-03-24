package com.sipandsavour.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sipandsavour.data.SessionManager;

public class SettingsViewModel extends ViewModel {

    private final MutableLiveData<String> currentLanguage = new MutableLiveData<>();

    public SettingsViewModel() {
        // Chargement de la langue actuelle au démarrage
        currentLanguage.setValue(SessionManager.getInstance().getLanguage());
    }

    public LiveData<String> getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * Sauvegarde temporaire du choix dans le ViewModel
     */
    public void setLanguage(String langCode) {
        currentLanguage.setValue(langCode);
    }

    /**
     * Persiste définitivement le changement dans SharedPreferences
     */
    public void saveSettings() {
        SessionManager.getInstance().setLanguage(currentLanguage.getValue());
    }
}