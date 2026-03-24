package com.sipandsavour.ui.result;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sipandsavour.data.Repository;
import com.sipandsavour.data.dto.WineDto;
import com.sipandsavour.ui.common.UiState;
import com.sipandsavour.util.TranslationManager;

public class WeeklyChoiceViewModel extends ViewModel {

    private final MutableLiveData<UiState<WineDto>> recommendationState = new MutableLiveData<>();

    public LiveData<UiState<WineDto>> getRecommendationState() {
        return recommendationState;
    }

    public void loadRecommendation() {
        recommendationState.setValue(UiState.loading());

        LiveData<UiState<WineDto>> source = Repository.getInstance().getWeeklyRecommendation();

        source.observeForever(new Observer<UiState<WineDto>>() {
            @Override
            public void onChanged(UiState<WineDto> state) {
                if (!state.isLoading()) {
                    source.removeObserver(this);

                    if (state.isSuccess() && state.getData() != null) {
                        // Traduction de la recommandation hebdomadaire
                        TranslationManager.getInstance().translateWineIfNeeded(state.getData(), translatedWine -> {
                            recommendationState.setValue(state);
                        });
                    } else {
                        recommendationState.setValue(state);
                    }
                }
            }
        });
    }
}