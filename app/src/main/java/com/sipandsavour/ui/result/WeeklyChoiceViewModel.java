package com.sipandsavour.ui.result;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sipandsavour.data.Repository;
import com.sipandsavour.data.dto.meal.MealDto;
import com.sipandsavour.ui.common.UiState;
import com.sipandsavour.util.MealTranslationManager;
import com.sipandsavour.util.TranslationManager;

public class WeeklyChoiceViewModel extends ViewModel {

    private final MutableLiveData<UiState<Repository.WeeklyPairingResult>> pairingState = new MutableLiveData<>();
    private final MutableLiveData<MealDto> translatedMeal = new MutableLiveData<>();

    /**
     * Get the state of the weekly pairing.
     * @return The LiveData for the weekly pairing state.
     */
    public LiveData<UiState<Repository.WeeklyPairingResult>> getPairingState() {
        return pairingState;
    }

    /**
     * Get the translated meal.
     * @return The LiveData for the translated meal.
     */
    public LiveData<MealDto> getTranslatedMeal() {
        return translatedMeal;
    }

    /**
     * Load the weekly recommendation.
     */
    public void loadRecommendation() {
        pairingState.setValue(UiState.loading());

        LiveData<UiState<Repository.WeeklyPairingResult>> source = Repository.getInstance().getWeeklyPairing();

        source.observeForever(new Observer<UiState<Repository.WeeklyPairingResult>>() {
            @Override
            public void onChanged(UiState<Repository.WeeklyPairingResult> state) {
                if (state.isLoading()) return;

                source.removeObserver(this);

                if (state.isSuccess() && state.getData() != null) {
                    Repository.WeeklyPairingResult result = state.getData();

                    // Traduire le vin
                    if (result.getWine() != null) {
                        TranslationManager.getInstance().translateWineIfNeeded(result.getWine(), translatedWine -> {
                            // Vin traduit, maintenant traduire le plat
                            if (result.getMeal() != null) {
                                MealTranslationManager.getInstance().translateMeal(result.getMeal(), translatedMealResult -> {
                                    translatedMeal.setValue(translatedMealResult);
                                });
                            }
                        });
                    }

                    pairingState.setValue(state);
                } else {
                    pairingState.setValue(state);
                }
            }
        });
    }
}