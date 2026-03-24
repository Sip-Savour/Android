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

    private final MutableLiveData<List<WineDto>> favorites = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isEmpty = new MutableLiveData<>(true);

    private WineDto lastRemovedWine;
    private int lastRemovedPosition;

    public LiveData<List<WineDto>> getFavorites() { return favorites; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsEmpty() { return isEmpty; }

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

    public void refresh() {
        loadFavorites();
    }

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