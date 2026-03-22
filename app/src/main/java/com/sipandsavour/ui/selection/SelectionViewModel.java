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
 * ViewModel pour gérer l'état de la sélection des saveurs et de la couleur.
 */
public class SelectionViewModel extends ViewModel {

    // Catégories accordéon
    private final MutableLiveData<List<FlavorMapper.AccordionCategory>> categories = new MutableLiveData<>();

    // Saveurs sélectionnées
    private final Set<String> selectedFlavors = new HashSet<>();
    private final MutableLiveData<Set<String>> selectedFlavorsLiveData = new MutableLiveData<>(new HashSet<>());

    // Couleur de vin sélectionnée
    private final MutableLiveData<String> selectedColor = new MutableLiveData<>(null);

    // Résultat de la prédiction API (NOUVEAU)
    private final MutableLiveData<UiState<PredictResponse>> predictionResult = new MutableLiveData<>();

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
    //  SAVEURS SÉLECTIONNÉES & COULEUR (INTERCEPTION)
    // =======================================================

    public LiveData<Set<String>> getSelectedFlavors() {
        return selectedFlavorsLiveData;
    }

    public void toggleFlavor(String flavorKey) {
        // 1. INTERCEPTION DES COULEURS (On capte le Français et l'Anglais par sécurité)
        if (flavorKey.equalsIgnoreCase("Rouge") || flavorKey.equalsIgnoreCase("red") ||
                flavorKey.equalsIgnoreCase("Blanc") || flavorKey.equalsIgnoreCase("white") ||
                flavorKey.equalsIgnoreCase("Rosé") || flavorKey.equalsIgnoreCase("rose")) {

            if (flavorKey.equals(selectedColor.getValue())) {
                selectedColor.setValue(null); // On décoche si c'était déjà sélectionné
            } else {
                selectedColor.setValue(flavorKey); // On applique la nouvelle couleur
            }
            // On notifie l'UI pour mettre à jour l'affichage
            selectedFlavorsLiveData.setValue(new HashSet<>(selectedFlavors));
            return; // 🛑 On s'arrête ici pour ne pas l'ajouter aux saveurs
        }

        // 2. COMPORTEMENT NORMAL (Saveurs)
        if (selectedFlavors.contains(flavorKey)) {
            selectedFlavors.remove(flavorKey);
        } else {
            selectedFlavors.add(flavorKey);
        }
        selectedFlavorsLiveData.setValue(new HashSet<>(selectedFlavors));
    }

    public boolean isFlavorSelected(String flavorKey) {
        // L'adaptateur a besoin de savoir si le bouton couleur doit être coché
        if (flavorKey.equalsIgnoreCase("Rouge") || flavorKey.equalsIgnoreCase("red") ||
                flavorKey.equalsIgnoreCase("Blanc") || flavorKey.equalsIgnoreCase("white") ||
                flavorKey.equalsIgnoreCase("Rosé") || flavorKey.equalsIgnoreCase("rose")) {
            return flavorKey.equals(selectedColor.getValue());
        }
        return selectedFlavors.contains(flavorKey);
    }

    public boolean hasSelection() {
        return !selectedFlavors.isEmpty();
    }

    public void clearSelections() {
        selectedFlavors.clear();
        selectedColor.setValue(null);
        selectedFlavorsLiveData.setValue(new HashSet<>());
    }

    // =======================================================
    //  COULEUR
    // =======================================================

    public LiveData<String> getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(String color) {
        selectedColor.setValue(color);
        // On notifie pour que l'UI se rafraîchisse si la couleur change via une autre méthode
        selectedFlavorsLiveData.setValue(new HashSet<>(selectedFlavors));
    }

    // =======================================================
    //  RÉSULTAT DE PRÉDICTION (NOUVEAU)
    // =======================================================

    public LiveData<UiState<PredictResponse>> getPredictionResult() {
        return predictionResult;
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
    //  PRÉDICTION (API)
    // =======================================================

    public void predict() {
        android.util.Log.d("API_TEST", "Lancement de la requête vers l'API...");

        // On avertit l'interface que le chargement commence
        predictionResult.setValue(UiState.loading());

        StringBuilder sb = new StringBuilder();
        for (String flavor : selectedFlavors) {
            if (sb.length() > 0) sb.append(" ");
            // On s'assure de remplacer les underscores par des espaces pour l'IA
            sb.append(flavor.replace("_", " "));
        }
        String features = sb.toString();

        // --- TRADUCTION DE LA COULEUR POUR L'API ---
        String rawColor = selectedColor.getValue();
        String apiColor = null;

        if (rawColor != null) {
            String lowerColor = rawColor.toLowerCase();
            if (lowerColor.equals("rouge") || lowerColor.equals("red")) {
                apiColor = "Red"; // L'API attend "Red"
            } else if (lowerColor.equals("blanc") || lowerColor.equals("white")) {
                apiColor = "White"; // L'API attend "White"
            } else if (lowerColor.equals("rosé") || lowerColor.equals("rose")) {
                apiColor = "Rose"; // L'API attend "Rose"
            }
        }

        android.util.Log.d("API_TEST", "Features envoyés : [" + features + "]");
        android.util.Log.d("API_TEST", "Couleur envoyée : [" + apiColor + "]");

        // On envoie la couleur traduite
        LiveData<UiState<PredictResponse>> repoResult = Repository.getInstance().predict(features, apiColor);

        repoResult.observeForever(new androidx.lifecycle.Observer<UiState<PredictResponse>>() {
            @Override
            public void onChanged(UiState<PredictResponse> state) {
                // On transmet l'état en direct à notre LiveData
                predictionResult.setValue(state);

                if (state.isLoading()) {
                    android.util.Log.d("API_TEST", "⏳ Chargement en cours...");
                }
                else if (state.isSuccess()) {
                    android.util.Log.d("API_TEST", "✅ Succès ! L'API a répondu.");
                    PredictResponse response = state.getData();

                    if (response != null && response.getBottle() != null) {
                        android.util.Log.d("API_TEST", "🍷 Nombre de vins trouvés : " + response.getBottle().size());
                        if (!response.getBottle().isEmpty()) {
                            // Affichage sécurisé en fonction des données récupérées
                            android.util.Log.d("API_TEST", "🥇 Premier vin : " +
                                    response.getBottle().get(0).getTitle() +" | "+
                                    response.getBottle().get(0).getVariety() +" | "+
                                    response.getBottle().get(0).getColor());
                        }
                    }
                    repoResult.removeObserver(this);
                }
                else if (state.isError()) {
                    android.util.Log.e("API_TEST", "❌ Erreur API : " + state.getMessage());
                    repoResult.removeObserver(this);
                }
            }
        });
    }
}