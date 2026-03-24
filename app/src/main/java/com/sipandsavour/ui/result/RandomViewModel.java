package com.sipandsavour.ui.result;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sipandsavour.data.api.ApiClient;
import com.sipandsavour.data.dto.WineDto;
import com.sipandsavour.ui.common.UiState;
import com.sipandsavour.util.TranslationManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RandomViewModel extends ViewModel {

    private final MutableLiveData<UiState<List<WineDto>>> randomWinesState = new MutableLiveData<>();

    public LiveData<UiState<List<WineDto>>> getRandomWinesState() {
        return randomWinesState;
    }

    public void loadRandomWines() {
        randomWinesState.setValue(UiState.loading());

        ApiClient.getInstance().getWineApi().getRandomWines().enqueue(new Callback<List<WineDto>>() {
            @Override
            public void onResponse(Call<List<WineDto>> call, Response<List<WineDto>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    TranslationManager.getInstance().translateWineListIfNeeded(response.body(), translatedList -> {
                        // CORRECTION : postValue au lieu de setValue pour éviter les crashs d'arrière-plan
                        randomWinesState.postValue(UiState.success(translatedList));
                    });

                } else {
                    randomWinesState.postValue(UiState.error("Erreur serveur : impossible d'obtenir des vins."));
                }
            }

            @Override
            public void onFailure(Call<List<WineDto>> call, Throwable t) {
                randomWinesState.postValue(UiState.error("Erreur de connexion : " + t.getMessage()));
            }
        });
    }
}