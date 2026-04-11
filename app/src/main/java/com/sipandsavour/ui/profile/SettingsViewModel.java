package com.sipandsavour.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sipandsavour.data.SessionManager;

public class SettingsViewModel extends ViewModel {

    private final MutableLiveData<String> currentLanguage = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentTheme = new MutableLiveData<>();

    /**
     * Constructeur de la classe SettingsViewModel.
     */
    public SettingsViewModel() {
        currentLanguage.setValue(SessionManager.getInstance().getLanguage());
        currentTheme.setValue(SessionManager.getInstance().getTheme());
    }

    /**
     * Retourne les données en direct pour la langue actuelle.
     */
    public LiveData<String> getCurrentLanguage() { return currentLanguage; }
    public LiveData<Integer> getCurrentTheme() { return currentTheme; }

    /**
     * Met à jour la langue actuelle.
     * @param langCode Le code de la langue sélectionnée.
     */
    public void setLanguage(String langCode) {
        currentLanguage.setValue(langCode);
    }

    /**
     * Met à jour le thème actuel.
     * @param themeMode Le mode de thème sélectionné.
     */
    public void setTheme(int themeMode) {
        currentTheme.setValue(themeMode);
    }

    /**
     * Enregistre les paramètres dans le gestionnaire de session.
     */
    public void saveSettings() {
        SessionManager.getInstance().setLanguage(currentLanguage.getValue());
        if (currentTheme.getValue() != null) {
            SessionManager.getInstance().setTheme(currentTheme.getValue());
        }
    }
}