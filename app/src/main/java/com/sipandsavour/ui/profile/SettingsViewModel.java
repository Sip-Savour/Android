package com.sipandsavour.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sipandsavour.data.SessionManager;

public class SettingsViewModel extends ViewModel {

    private final MutableLiveData<String> currentLanguage = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentTheme = new MutableLiveData<>();

    public SettingsViewModel() {
        currentLanguage.setValue(SessionManager.getInstance().getLanguage());
        currentTheme.setValue(SessionManager.getInstance().getTheme());
    }

    public LiveData<String> getCurrentLanguage() { return currentLanguage; }
    public LiveData<Integer> getCurrentTheme() { return currentTheme; }

    public void setLanguage(String langCode) {
        currentLanguage.setValue(langCode);
    }

    public void setTheme(int themeMode) {
        currentTheme.setValue(themeMode);
    }

    public void saveSettings() {
        SessionManager.getInstance().setLanguage(currentLanguage.getValue());
        if (currentTheme.getValue() != null) {
            SessionManager.getInstance().setTheme(currentTheme.getValue());
        }
    }
}