package com.sipandsavour.ui.favorites;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sipandsavour.data.Repository;
import com.sipandsavour.data.dto.WineDto;
import com.sipandsavour.ui.common.UiState;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel pour l'écran des favoris.
 */
public class FavoritesViewModel extends ViewModel {

    private final MutableLiveData<List<WineDto>> favorites = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isEmpty = new MutableLiveData<>(true);

    // Pour undo
    private WineDto lastRemovedWine;
    private int lastRemovedPosition;

    public LiveData<List<WineDto>> getFavorites() { return favorites; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsEmpty() { return isEmpty; }

    // =======================================================
    //  LOAD FAVORITES (VRAI APPEL API)
    // =======================================================

    public void loadFavorites() {
        isLoading.setValue(true);

        LiveData<UiState<List<WineDto>>> source = Repository.getInstance().getFavorites();
        source.observeForever(new Observer<UiState<List<WineDto>>>() {
            @Override
            public void onChanged(UiState<List<WineDto>> state) {
                if (!state.isLoading()) {
                    source.removeObserver(this); // On arrête d'écouter
                    isLoading.setValue(false);

                    if (state.isSuccess() && state.getData() != null) {
                        favorites.setValue(state.getData());
                        isEmpty.setValue(state.getData().isEmpty());
                    } else {
                        // En cas d'erreur ou de liste vide
                        isEmpty.setValue(true);
                        favorites.setValue(new ArrayList<>());
                    }
                }
            }
        });
    }

    public void refresh() {
        loadFavorites();
    }

    // =======================================================
    //  REMOVE FAVORITE (AVEC APPEL API)
    // =======================================================

    public void removeFavorite(int position) {
        List<WineDto> currentList = favorites.getValue();
        if (currentList == null || position < 0 || position >= currentList.size()) return;

        WineDto wine = currentList.get(position);

        // Sauvegarder pour undo
        lastRemovedWine = wine;
        lastRemovedPosition = position;

        // 1. Supprimer localement (Mise à jour UI)
        List<WineDto> updatedList = new ArrayList<>(currentList);
        updatedList.remove(position);
        favorites.setValue(updatedList);
        isEmpty.setValue(updatedList.isEmpty());

        // 2. Envoyer la suppression à l'API
        Repository.getInstance().removeFavorite(wine.getId());
    }

    public void undoRemove() {
        if (lastRemovedWine == null) return;

        List<WineDto> currentList = favorites.getValue();
        if (currentList == null) currentList = new ArrayList<>();

        List<WineDto> updatedList = new ArrayList<>(currentList);
        int insertPosition = Math.min(lastRemovedPosition, updatedList.size());
        updatedList.add(insertPosition, lastRemovedWine);

        // 1. Remettre localement (Mise à jour UI)
        favorites.setValue(updatedList);
        isEmpty.setValue(false);

        // 2. Renvoyer l'ajout à l'API
        Repository.getInstance().addFavorite(lastRemovedWine.getId());

        lastRemovedWine = null;
    }
}