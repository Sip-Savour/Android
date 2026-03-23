package com.sipandsavour.ui.result;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sipandsavour.data.Repository;
import com.sipandsavour.data.SessionManager;
import com.sipandsavour.data.dto.WineDto;
import com.sipandsavour.ui.common.UiState;

import java.util.List;

public class ResultViewModel extends ViewModel {

    private final MutableLiveData<WineDto> currentWine = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>(false);

    private List<WineDto> wineList;
    private int currentIndex = -1;

    public LiveData<WineDto> getCurrentWine() {
        return currentWine;
    }

    public void setWineList(List<WineDto> list) {
        this.wineList = list;
    }

    public void setCurrentWine(WineDto wine) {
        currentWine.setValue(wine);
        isFavorite.setValue(false); // Par défaut, on met à false en attendant la réponse de l'API

        if (wine != null) {
            SessionManager.getInstance().addWineToHistory(wine.getId());
            checkIfFavorite(wine); // Vérifie si le vin est dans les favoris

            if (wineList != null) {
                currentIndex = -1;
                // Recherche de la position du vin actuel dans la liste
                for (int i = 0; i < wineList.size(); i++) {
                    if (wineList.get(i).getId() == wine.getId() ||
                            (wineList.get(i).getTitle() != null && wineList.get(i).getTitle().equals(wine.getTitle()))) {
                        currentIndex = i;
                        break;
                    }
                }
            }
        }
    }

    /**
     * Interroge l'API pour savoir si ce vin précis fait partie des favoris de l'utilisateur
     */
    private void checkIfFavorite(WineDto wine) {
        LiveData<UiState<List<WineDto>>> source = Repository.getInstance().getFavorites();
        source.observeForever(new Observer<UiState<List<WineDto>>>() {
            @Override
            public void onChanged(UiState<List<WineDto>> state) {
                if (!state.isLoading()) {
                    source.removeObserver(this); // On se désabonne pour ne pas boucler
                    if (state.isSuccess() && state.getData() != null) {
                        boolean found = false;
                        for (WineDto fav : state.getData()) {
                            if (fav.getId() == wine.getId()) {
                                found = true;
                                break;
                            }
                        }
                        isFavorite.setValue(found);
                    }
                }
            }
        });
    }

    public boolean nextWine() {
        if (wineList != null && currentIndex >= 0 && currentIndex < wineList.size() - 1) {
            currentIndex++;
            setCurrentWine(wineList.get(currentIndex)); // On réutilise setCurrentWine pour déclencher la vérification favori
            return true;
        }
        return false;
    }

    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }

    /**
     * Appelé quand on clique sur le coeur. Ajoute ou supprime de l'API.
     */
    public void toggleFavorite() {
        WineDto wine = currentWine.getValue();
        if (wine == null) return;

        Boolean current = isFavorite.getValue();
        boolean isFav = current != null ? current : false;

        // 1. Mise à jour immédiate de l'interface (pour que le coeur change tout de suite)
        isFavorite.setValue(!isFav);

        // 2. Appel à l'API en arrière-plan
        if (isFav) {
            // C'était un favori, on le retire
            Repository.getInstance().removeFavorite(wine.getId());
        } else {
            // Ce n'était pas un favori, on l'ajoute
            Repository.getInstance().addFavorite(wine.getId());
        }
    }
}