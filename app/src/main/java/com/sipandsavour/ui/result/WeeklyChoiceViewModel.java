package com.sipandsavour.ui.result;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sipandsavour.data.Repository;
import com.sipandsavour.data.dto.WineDto;
import com.sipandsavour.ui.common.UiState;

public class WeeklyChoiceViewModel extends ViewModel {

    private final MutableLiveData<UiState<WineDto>> recommendationState = new MutableLiveData<>();

    public LiveData<UiState<WineDto>> getRecommendationState() {
        return recommendationState;
    }

    public void loadRecommendation() {
        // On signale à l'interface que le chargement commence
        recommendationState.setValue(UiState.loading());

        // On appelle la nouvelle méthode du Repository (qui gère la prédiction + le tirage au sort)
        LiveData<UiState<WineDto>> source = Repository.getInstance().getWeeklyRecommendation();

        source.observeForever(new Observer<UiState<WineDto>>() {
            @Override
            public void onChanged(UiState<WineDto> state) {
                if (!state.isLoading()) {
                    recommendationState.setValue(state);
                    source.removeObserver(this); // On se désabonne pour éviter les fuites de mémoire
                }
            }
        });
    }
}