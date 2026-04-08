package com.sipandsavour.ui.favorites;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sipandsavour.data.Repository;
import com.sipandsavour.data.dto.WineDto;
import com.sipandsavour.ui.common.UiState;
import com.sipandsavour.util.TranslationManager;

import java.util.ArrayList;
import java.util.List;

public class FavoritesViewModel extends ViewModel {

    /**
     * LiveData pour la liste des vins favoris, l'état de chargement et si la liste est vide.
     */
    private final MutableLiveData<List<WineDto>> favorites = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isEmpty = new MutableLiveData<>(true);

    /** 
     * Variables pour stocker temporairement le dernier vin supprimé et sa position, afin de permettre l'annulation de la suppression.
     */
    private WineDto lastRemovedWine;
    private int lastRemovedPosition;

    /**
     * Getters pour les LiveData afin que la vue puisse les observer.
     */
    public LiveData<List<WineDto>> getFavorites() { return favorites; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsEmpty() { return isEmpty; }


    /**
     * Charge les vins favoris depuis le Repository, gère l'état de chargement et traduit la liste si nécessaire.
     */
    public void loadFavorites() {
        isLoading.setValue(true);

        LiveData<UiState<List<WineDto>>> source = Repository.getInstance().getFavorites();
        source.observeForever(new Observer<UiState<List<WineDto>>>() {
            @Override
            public void onChanged(UiState<List<WineDto>> state) {
                if (!state.isLoading()) {
                    source.removeObserver(this);
                    isLoading.setValue(false);

                    if (state.isSuccess() && state.getData() != null) {
                        // Traduction de la liste complète des favoris
                        TranslationManager.getInstance().translateWineListIfNeeded(state.getData(), translatedList -> {
                            favorites.setValue(translatedList);
                            isEmpty.setValue(translatedList.isEmpty());
                        });
                    } else {
                        isEmpty.setValue(true);
                        favorites.setValue(new ArrayList<>());
                    }
                }
            }
        });
    }

    /**
     * Rafraîchit la liste des vins favoris.
     */
    public void refresh() {
        loadFavorites();
    }

    /**
     * Supprime un vin des favoris.
     * @param position La position du vin à supprimer dans la liste actuelle des favoris.
     */
    public void removeFavorite(int position) {
        List<WineDto> currentList = favorites.getValue();
        if (currentList == null || position < 0 || position >= currentList.size()) return;

        WineDto wine = currentList.get(position);

        lastRemovedWine = wine;
        lastRemovedPosition = position;

        List<WineDto> updatedList = new ArrayList<>(currentList);
        updatedList.remove(position);
        favorites.setValue(updatedList);
        isEmpty.setValue(updatedList.isEmpty());

        Repository.getInstance().removeFavorite(wine.getId());
    }

    /**
     * Annule la suppression d'un vin des favoris.
     */
    public void undoRemove() {
        if (lastRemovedWine == null) return;

        List<WineDto> currentList = favorites.getValue();
        if (currentList == null) currentList = new ArrayList<>();

        List<WineDto> updatedList = new ArrayList<>(currentList);
        int insertPosition = Math.min(lastRemovedPosition, updatedList.size());
        updatedList.add(insertPosition, lastRemovedWine);

        favorites.setValue(updatedList);
        isEmpty.setValue(false);

        Repository.getInstance().addFavorite(lastRemovedWine.getId());

        lastRemovedWine = null;
    }
}