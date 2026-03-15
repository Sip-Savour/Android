package com.sipandsavour.ui.selection;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sipandsavour.logic.FlavorMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ViewModel pour gérer l'état de la sélection des saveurs.
 */
public class SelectionViewModel extends ViewModel {

    // Catégories accordéon
    private final MutableLiveData<List<FlavorMapper.AccordionCategory>> categories = new MutableLiveData<>();

    // Saveurs sélectionnées
    private final Set<String> selectedFlavors = new HashSet<>();
    private final MutableLiveData<Set<String>> selectedFlavorsLiveData = new MutableLiveData<>(new HashSet<>());

    // Couleur de vin sélectionnée
    private final MutableLiveData<String> selectedColor = new MutableLiveData<>(null);

    // Mode de recherche
    private String mode = "match";

    public SelectionViewModel() {
        loadCategories();
    }

    // =======================================================
    //  CATÉGORIES
    // =======================================================

    private void loadCategories() {
        List<FlavorMapper.AccordionCategory> cats = FlavorMapper.getAccordionCategories();
        categories.setValue(cats);
    }

    public LiveData<List<FlavorMapper.AccordionCategory>> getCategories() {
        return categories;
    }

    public void toggleCategory(int position) {
        List<FlavorMapper.AccordionCategory> cats = categories.getValue();
        if (cats != null && position >= 0 && position < cats.size()) {
            cats.get(position).toggleExpanded();
            categories.setValue(cats);
        }
    }

    // =======================================================
    //  SAVEURS SÉLECTIONNÉES
    // =======================================================

    public LiveData<Set<String>> getSelectedFlavors() {
        return selectedFlavorsLiveData;
    }

    public void toggleFlavor(String flavorKey) {
        if (selectedFlavors.contains(flavorKey)) {
            selectedFlavors.remove(flavorKey);
        } else {
            selectedFlavors.add(flavorKey);
        }
        selectedFlavorsLiveData.setValue(new HashSet<>(selectedFlavors));
    }

    public boolean isFlavorSelected(String flavorKey) {
        return selectedFlavors.contains(flavorKey);
    }

    public boolean hasSelection() {
        return !selectedFlavors.isEmpty();
    }

    public void clearSelections() {
        selectedFlavors.clear();
        selectedFlavorsLiveData.setValue(new HashSet<>());
        selectedColor.setValue(null);
    }

    // =======================================================
    //  COULEUR
    // =======================================================

    public LiveData<String> getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(String color) {
        selectedColor.setValue(color);
    }

    // =======================================================
    //  MODE
    // =======================================================

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    // =======================================================
    //  PRÉDICTION
    // =======================================================

    public void predict() {
        // TODO: Construire la chaîne de features avec FlavorMapper
        // TODO: Appeler Repository.predict(features, color)
        // TODO: Mettre à jour un LiveData<UiState<PredictResponse>>
    }
}