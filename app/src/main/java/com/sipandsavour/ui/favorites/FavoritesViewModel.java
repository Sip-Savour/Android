package com.sipandsavour.ui.favorites;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sipandsavour.data.dto.WineDto;

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

    // =======================================================
    //  LOAD FAVORITES
    // =======================================================

    public LiveData<List<WineDto>> getFavorites() {
        return favorites;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsEmpty() {
        return isEmpty;
    }

    public void loadFavorites() {
        isLoading.setValue(true);

        // TODO: Appeler Repository.getFavorites()
        // TODO: Mettre à jour favorites, isLoading, isEmpty selon le résultat

        // Temporaire : simuler un chargement
        isLoading.setValue(false);
        isEmpty.setValue(true);
    }

    public void refresh() {
        loadFavorites();
    }

    // =======================================================
    //  REMOVE FAVORITE
    // =======================================================

    public void removeFavorite(int position) {
        List<WineDto> currentList = favorites.getValue();
        if (currentList == null || position < 0 || position >= currentList.size()) return;

        WineDto wine = currentList.get(position);

        // Sauvegarder pour undo
        lastRemovedWine = wine;
        lastRemovedPosition = position;

        // Supprimer localement
        List<WineDto> updatedList = new ArrayList<>(currentList);
        updatedList.remove(position);
        favorites.setValue(updatedList);
        isEmpty.setValue(updatedList.isEmpty());

        // TODO: Appeler Repository.removeFavorite(wine.getId())
    }

    public void undoRemove() {
        if (lastRemovedWine == null) return;

        List<WineDto> currentList = favorites.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        } else {
            currentList = new ArrayList<>(currentList);
        }

        int insertPosition = Math.min(lastRemovedPosition, currentList.size());
        currentList.add(insertPosition, lastRemovedWine);

        favorites.setValue(currentList);
        isEmpty.setValue(false);

        // TODO: Appeler Repository.addFavorite(lastRemovedWine.getId())

        lastRemovedWine = null;
    }
}