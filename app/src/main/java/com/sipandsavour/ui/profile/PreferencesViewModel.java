package com.sipandsavour.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sipandsavour.data.SessionManager;

import java.util.HashSet;
import java.util.Set;

public class PreferencesViewModel extends ViewModel {

    private final MutableLiveData<String> selectedColor = new MutableLiveData<>();
    private final MutableLiveData<Set<String>> selectedFeatures = new MutableLiveData<>();

    public PreferencesViewModel() {
        loadPreferences();
    }

    private void loadPreferences() {
        // Chargement de la couleur
        selectedColor.setValue(SessionManager.getInstance().getPreferredColor());

        // Chargement des arômes
        Set<String> features = SessionManager.getInstance().getPreferredFeatures();
        if (features == null) {
            features = new HashSet<>();
        }
        selectedFeatures.setValue(new HashSet<>(features)); // On fait une copie
    }

    public LiveData<String> getSelectedColor() { return selectedColor; }
    public LiveData<Set<String>> getSelectedFeatures() { return selectedFeatures; }

    public void setColor(String color) {
        selectedColor.setValue(color);
    }

    public void toggleFeature(String feature) {
        Set<String> current = selectedFeatures.getValue();
        if (current == null) current = new HashSet<>();

        Set<String> updated = new HashSet<>(current);
        if (updated.contains(feature)) {
            updated.remove(feature);
        } else {
            updated.add(feature);
        }
        selectedFeatures.setValue(updated);
    }

    public void save() {
        SessionManager.getInstance().setPreferredColor(selectedColor.getValue());
        SessionManager.getInstance().setPreferredFeatures(selectedFeatures.getValue());
    }
}