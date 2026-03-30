package com.sipandsavour.ui.selection;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sipandsavour.data.Repository;
import com.sipandsavour.data.dto.PredictResponse;
import com.sipandsavour.data.dto.meal.MealDto;
import com.sipandsavour.data.dto.meal.MealFilterResponse;
import com.sipandsavour.logic.FlavorMapper;
import com.sipandsavour.ui.common.UiState;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectionViewModel extends ViewModel {

    private final MutableLiveData<List<FlavorMapper.AccordionCategory>> categories = new MutableLiveData<>();
    private final Set<String> selectedFlavors = new HashSet<>();
    private final MutableLiveData<Set<String>> selectedFlavorsLiveData = new MutableLiveData<>(new HashSet<>());
    private final MutableLiveData<String> selectedColor = new MutableLiveData<>(null);
    private final MutableLiveData<UiState<PredictResponse>> predictionResult = new MutableLiveData<>();
    private String mode = "match";

    public SelectionViewModel() {
        loadCategories();
    }

    private void loadCategories() {
        List<FlavorMapper.AccordionCategory> cats = FlavorMapper.getAccordionCategories();
        categories.setValue(cats);
    }

    public LiveData<List<FlavorMapper.AccordionCategory>> getCategories() { return categories; }

    public void toggleCategory(int position) {
        List<FlavorMapper.AccordionCategory> cats = categories.getValue();
        if (cats != null && position >= 0 && position < cats.size()) {
            cats.get(position).toggleExpanded();
            categories.setValue(cats);
        }
    }

    public LiveData<Set<String>> getSelectedFlavors() { return selectedFlavorsLiveData; }

    public boolean hasSelection() {
        return !selectedFlavors.isEmpty();
    }

    public void toggleFlavor(String flavorKey) {
        if (flavorKey.equalsIgnoreCase("Rouge") || flavorKey.equalsIgnoreCase("red") ||
                flavorKey.equalsIgnoreCase("Blanc") || flavorKey.equalsIgnoreCase("white") ||
                flavorKey.equalsIgnoreCase("Rosé") || flavorKey.equalsIgnoreCase("rose")) {

            if (flavorKey.equals(selectedColor.getValue())) selectedColor.setValue(null);
            else selectedColor.setValue(flavorKey);

            selectedFlavorsLiveData.setValue(new HashSet<>(selectedFlavors));
            return;
        }

        if (selectedFlavors.contains(flavorKey)) selectedFlavors.remove(flavorKey);
        else selectedFlavors.add(flavorKey);

        selectedFlavorsLiveData.setValue(new HashSet<>(selectedFlavors));
    }

    public boolean isFlavorSelected(String flavorKey) {
        if (flavorKey.equalsIgnoreCase("Rouge") || flavorKey.equalsIgnoreCase("red") ||
                flavorKey.equalsIgnoreCase("Blanc") || flavorKey.equalsIgnoreCase("white") ||
                flavorKey.equalsIgnoreCase("Rosé") || flavorKey.equalsIgnoreCase("rose")) {
            return flavorKey.equals(selectedColor.getValue());
        }
        return selectedFlavors.contains(flavorKey);
    }

    public void clearSelections() {
        selectedFlavors.clear();
        selectedColor.setValue(null);
        selectedFlavorsLiveData.setValue(new HashSet<>());
    }

    public LiveData<String> getSelectedColor() { return selectedColor; }

    public void setSelectedColor(String color) {
        selectedColor.setValue(color);
        selectedFlavorsLiveData.setValue(new HashSet<>(selectedFlavors));
    }

    public LiveData<UiState<PredictResponse>> getPredictionResult() { return predictionResult; }
    public void setMode(String mode) { this.mode = mode; }
    public String getMode() { return mode; }

    // =======================================================
    //  MÉTHODES PUBLIQUES POUR L'UI
    // =======================================================

    /**
     * Récupère les suggestions de repas (TRADUIT)
     */
    public LiveData<UiState<MealFilterResponse>> getMealSuggestions() {
        return getMealSuggestionsTranslated("Beef");
    }

    /**
     * Récupère les détails complets d'une recette par son ID (TRADUIT)
     */
    public LiveData<UiState<MealFilterResponse>> getMealDetails(String mealId) {
        return getMealDetailsTranslated(mealId);
    }

    // =======================================================
    //  TRADUCTION DES REPAS
    // =======================================================

    /**
     * Récupère et traduit les détails d'un repas
     */
    private LiveData<UiState<MealFilterResponse>> getMealDetailsTranslated(String mealId) {
        MutableLiveData<UiState<MealFilterResponse>> result = new MutableLiveData<>();
        result.setValue(UiState.loading());

        LiveData<UiState<MealFilterResponse>> sourceData = Repository.getInstance().getMealDetails(mealId);

        sourceData.observeForever(new Observer<UiState<MealFilterResponse>>() {
            @Override
            public void onChanged(UiState<MealFilterResponse> state) {
                if (state.isLoading()) return;

                sourceData.removeObserver(this);

                if (state.isSuccess() && state.getData() != null &&
                    state.getData().getMeals() != null && !state.getData().getMeals().isEmpty()) {

                    MealDto originalMeal = state.getData().getMeals().get(0);

                    // Traduire le repas via LibreTranslate
                    com.sipandsavour.util.MealTranslationManager.getInstance()
                        .translateMeal(originalMeal, translatedMeal -> {
                            if (translatedMeal != null) {
                                MealFilterResponse translatedResponse = new MealFilterResponse();
                                translatedResponse.setMeals(java.util.Collections.singletonList(translatedMeal));
                                result.setValue(UiState.success(translatedResponse));
                            } else {
                                result.setValue(state); // Fallback sur l'original
                            }
                        });

                } else {
                    result.setValue(state);
                }
            }
        });

        return result;
    }

    /**
 * Récupère 5 recettes aléatoires et les traduit
 */
private LiveData<UiState<MealFilterResponse>> getMealSuggestionsTranslated(String category) {
    MutableLiveData<UiState<MealFilterResponse>> result = new MutableLiveData<>();
    result.setValue(UiState.loading());

    LiveData<UiState<MealFilterResponse>> sourceData = Repository.getInstance().getMealsByCategory(category);

    sourceData.observeForever(new Observer<UiState<MealFilterResponse>>() {
        @Override
        public void onChanged(UiState<MealFilterResponse> state) {
            if (state.isLoading()) return;

            sourceData.removeObserver(this);

            if (state.isSuccess() && state.getData() != null &&
                state.getData().getMeals() != null && !state.getData().getMeals().isEmpty()) {

                List<MealDto> allMeals = state.getData().getMeals();

                // 🆕 Sélectionner 5 recettes aléatoires
                List<MealDto> randomMeals = getRandomMeals(allMeals, 5);

                List<MealDto> translatedMeals = new java.util.ArrayList<>();

                final int[] translatedCount = {0};
                final int totalMeals = randomMeals.size();

                for (MealDto meal : randomMeals) {
                    com.sipandsavour.util.MealTranslationManager.getInstance()
                        .translateMeal(meal, translatedMeal -> {
                            synchronized (translatedMeals) {
                                translatedMeals.add(translatedMeal != null ? translatedMeal : meal);
                                translatedCount[0]++;

                                if (translatedCount[0] == totalMeals) {
                                    MealFilterResponse translatedResponse = new MealFilterResponse();
                                    translatedResponse.setMeals(translatedMeals);
                                    result.setValue(UiState.success(translatedResponse));
                                }
                            }
                        });
                }

            } else {
                result.setValue(state);
            }
        }
    });

    return result;
}

/**
 * Sélectionne N recettes aléatoires depuis une liste
 */
private List<MealDto> getRandomMeals(List<MealDto> meals, int count) {
    if (meals == null || meals.isEmpty()) {
        return new java.util.ArrayList<>();
    }

    // Copier la liste pour ne pas modifier l'originale
    List<MealDto> shuffled = new java.util.ArrayList<>(meals);

    // Mélanger aléatoirement
    java.util.Collections.shuffle(shuffled);

    // Retourner les N premiers (ou moins si pas assez)
    int limit = Math.min(count, shuffled.size());
    return shuffled.subList(0, limit);
}

    // =======================================================
    //  MAPPING INTELLIGENT AVEC GESTION DE LA COULEUR OPTIONNELLE
    // =======================================================

    public void predictFromMeal(Set<String> mealTastes) {
        clearSelections();

        // 1. COULEUR MANUELLE (Optionnelle, envoyée uniquement si l'utilisateur la choisit)
        if (mealTastes.contains("Vin Rouge")) {
            selectedColor.setValue("red");
        } else if (mealTastes.contains("Vin Blanc")) {
            selectedColor.setValue("white");
        } else if (mealTastes.contains("Vin Rosé")) {
            selectedColor.setValue("rose");
        } else {
            selectedColor.setValue(null); // Si aucune couleur choisie, on laisse l'API gérer !
        }

        // 2. BASE DU PLAT (Génère uniquement des arômes, plus de couleur forcée)
        if (mealTastes.contains("Viande Rouge")) {
            selectedFlavors.add("tannin");
            selectedFlavors.add("black fruit");
            selectedFlavors.add("oak");
        }
        else if (mealTastes.contains("Viande Blanche") || mealTastes.contains("Volaille")) {
            selectedFlavors.add("tree fruit");
            selectedFlavors.add("light bodied");
        }
        else if (mealTastes.contains("Poisson") || mealTastes.contains("Fruits de mer")) {
            selectedFlavors.add("citrus");
            selectedFlavors.add("mineral");
            selectedFlavors.add("high acidity");
        }
        else if (mealTastes.contains("Végétarien")) {
            selectedFlavors.add("earthy");
            selectedFlavors.add("herb");
        }
        else if (mealTastes.contains("Fromage")) {
            selectedFlavors.add("oak");
            selectedFlavors.add("aged");
        }

        // 3. PROFIL GUSTATIF
        if (mealTastes.contains("Gras") || mealTastes.contains("Riche")) {
            selectedFlavors.add("butter");
            selectedFlavors.add("full body");
        }
        if (mealTastes.contains("Sec")) selectedFlavors.add("dry");
        if (mealTastes.contains("Salé")) {
            selectedFlavors.add("mineral");
            selectedFlavors.add("crisp");
        }
        if (mealTastes.contains("Sucré")) {
            selectedFlavors.add("sweet");
            selectedFlavors.add("honey");
            selectedFlavors.add("tropical fruit");
        }
        if (mealTastes.contains("Poivré") || mealTastes.contains("Épicé")) {
            selectedFlavors.add("pepper");
            selectedFlavors.add("spice");
        }
        if (mealTastes.contains("Acide")) {
            selectedFlavors.add("high acidity");
            selectedFlavors.add("citrus");
        }

        selectedFlavorsLiveData.setValue(new HashSet<>(selectedFlavors));

        // 4. On lance la prédiction
        predict();
    }

    public void predict() {
        android.util.Log.d("API_TEST", "Lancement de la préparation de la requête...");
        predictionResult.setValue(UiState.loading());

        StringBuilder sb = new StringBuilder();

        // On s'assure de ne prendre QUE ce qui est actuellement dans le Set
        for (String flavor : selectedFlavors) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(flavor.replace("_", " "));
        }
        String features = sb.toString();

        String rawColor = selectedColor.getValue();
        String apiColor = null;

        if (rawColor != null) {
            String lowerColor = rawColor.toLowerCase();
            if (lowerColor.equals("rouge") || lowerColor.equals("red")) apiColor = "Red";
            else if (lowerColor.equals("blanc") || lowerColor.equals("white")) apiColor = "White";
            else if (lowerColor.equals("rosé") || lowerColor.equals("rose")) apiColor = "Rose";
        }

        android.util.Log.d("API_TEST", "=======================================");
        android.util.Log.d("API_TEST", "🚀 ENVOI À L'API :");
        android.util.Log.d("API_TEST", "👉 Features : [" + features + "]");
        android.util.Log.d("API_TEST", "👉 Couleur  : [" + apiColor + "]");
        android.util.Log.d("API_TEST", "=======================================");

        LiveData<UiState<PredictResponse>> repoResult = Repository.getInstance().predict(features, apiColor);
        repoResult.observeForever(new Observer<UiState<PredictResponse>>() {
            @Override
            public void onChanged(UiState<PredictResponse> state) {
                predictionResult.setValue(state);

                if (state.isLoading()) {
                    android.util.Log.d("API_TEST", "⏳ Requête en cours, on attend la réponse...");
                }
                else if (state.isSuccess()) {
                    android.util.Log.d("API_TEST", "✅ SUCCÈS : L'API a répondu correctement !");

                    if (state.getData() != null && state.getData().getBottle() != null) {
                        int count = state.getData().getBottle().size();
                        android.util.Log.d("API_TEST", "🍷 Vins reçus : " + count);

                        for (int i = 0; i < count; i++) {
                            com.sipandsavour.data.dto.BottleResponse bottle = state.getData().getBottle().get(i);
                            android.util.Log.d("API_TEST", "   > Vin #" + (i+1) + " | ID: " + bottle.getId() + " | Nom: " + bottle.getTitle());
                        }

                        if (count > 0) {
                            int idToTest = state.getData().getBottle().get(0).getId();
                            android.util.Log.d("API_TEST", "🔍 TEST DE RÉCUPÉRATION : On lance getWineById pour l'ID " + idToTest);

                            LiveData<UiState<com.sipandsavour.data.dto.WineDto>> getWineResult = Repository.getInstance().getWineById(idToTest);
                            getWineResult.observeForever(new Observer<UiState<com.sipandsavour.data.dto.WineDto>>() {
                                @Override
                                public void onChanged(UiState<com.sipandsavour.data.dto.WineDto> wineState) {
                                    if (wineState.isLoading()) {
                                        android.util.Log.d("API_TEST", "   > ⏳ Requête GET /wines/" + idToTest + " en cours...");
                                    } else if (wineState.isSuccess() && wineState.getData() != null) {
                                        com.sipandsavour.data.dto.WineDto fetchedWine = wineState.getData();
                                        android.util.Log.d("API_TEST", "   > ✅ SUCCÈS GET PAR ID ! Vin récupéré : [" + fetchedWine.getId() + "] " + fetchedWine.getTitle());
                                        getWineResult.removeObserver(this);
                                    } else if (wineState.isError()) {
                                        android.util.Log.e("API_TEST", "   > ❌ ERREUR GET PAR ID : " + wineState.getMessage());
                                        getWineResult.removeObserver(this);
                                    }
                                }
                            });
                        }

                    } else {
                        android.util.Log.d("API_TEST", "⚠️ L'API a répondu, mais la liste des vins est vide ou nulle.");
                    }
                    android.util.Log.d("API_TEST", "=======================================");

                    repoResult.removeObserver(this);
                }
                else if (state.isError()) {
                    android.util.Log.e("API_TEST", "❌ ERREUR API : " + state.getMessage());
                    repoResult.removeObserver(this);
                }
            }
        });
    }
}