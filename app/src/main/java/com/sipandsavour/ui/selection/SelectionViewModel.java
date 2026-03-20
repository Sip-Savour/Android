package com.sipandsavour.ui.selection;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sipandsavour.data.Repository;
import com.sipandsavour.data.dto.PredictResponse;
import com.sipandsavour.logic.FlavorMapper;
import com.sipandsavour.ui.common.UiState;

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

    // =======================================================
    //  PRÉDICTION (TEST STATIQUE)
    // =======================================================

    public void predict() {
        android.util.Log.d("API_TEST", "Lancement de la requête de test vers l'API...");


        StringBuilder sb = new StringBuilder();
        for (String flavor : selectedFlavors) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(flavor);
        }
        String features = sb.toString();
        // 1. DONNÉES STATIQUES DE TEST
        String testColor = "White";

        // 2. APPEL AU REPOSITORY
        LiveData<UiState<PredictResponse>> repoResult = Repository.getInstance().predict(features, testColor);

        // 3. OBSERVATION DU RÉSULTAT (Pour le test dans les logs)
        repoResult.observeForever(new androidx.lifecycle.Observer<UiState<PredictResponse>>() {
            @Override
            public void onChanged(UiState<PredictResponse> state) {
                if (state.isLoading()) {
                    android.util.Log.d("API_TEST", "⏳ Chargement en cours...");
                }
                else if (state.isSuccess()) {
                    android.util.Log.d("API_TEST", "✅ Succès ! L'API a répondu.");
                    PredictResponse response = state.getData();

                    if (response != null && response.getBottle() != null) {
                        android.util.Log.d("API_TEST", "🍷 Nombre de vins trouvés : " + response.getBottle().size());
                        if (!response.getBottle().isEmpty()) {
                            android.util.Log.d("API_TEST", "🥇 Premier vin : " + response.getBottle().get(0).getTitle());
                        }
                    }
                    repoResult.removeObserver(this);
                }
                else if (state.isError()) {
                    android.util.Log.e("API_TEST", "❌ Erreur API : " + state.getMessage());
                    // Nettoyage de l'observateur de test
                    repoResult.removeObserver(this);
                }
            }
        });
    }
}