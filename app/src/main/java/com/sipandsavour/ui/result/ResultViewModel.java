package com.sipandsavour.ui.result;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sipandsavour.data.dto.WineDto;

/**
 * ViewModel pour l'écran de détail d'un vin.
 */
public class ResultViewModel extends ViewModel {

    private final MutableLiveData<WineDto> currentWine = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>(false);

    // =======================================================
    //  WINE DATA
    // =======================================================

    public LiveData<WineDto> getCurrentWine() {
        return currentWine;
    }

    public void setCurrentWine(WineDto wine) {
        currentWine.setValue(wine);
        // TODO: Vérifier si le vin est déjà en favori via Repository
    }

    // =======================================================
    //  FAVORITES
    // =======================================================

    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }

    public void toggleFavorite() {
        WineDto wine = currentWine.getValue();
        if (wine == null) return;

        Boolean current = isFavorite.getValue();
        if (current == null) current = false;

        // TODO: Appeler Repository.addFavorite() ou removeFavorite()
        // TODO: Mettre à jour isFavorite selon le résultat

        // Temporaire : toggle local
        isFavorite.setValue(!current);
    }
}