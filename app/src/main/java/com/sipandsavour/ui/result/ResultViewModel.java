package com.sipandsavour.ui.result;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sipandsavour.data.Repository;
import com.sipandsavour.data.SessionManager;
import com.sipandsavour.data.dto.WineDto;
import com.sipandsavour.ui.common.UiState;
import com.sipandsavour.util.TranslationManager;

import java.util.List;

public class ResultViewModel extends ViewModel {

    // Le vin actuel 
    private final MutableLiveData<WineDto> currentWine = new MutableLiveData<>();
    /// L'état de favori pour le vin actuel
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>(false);

    // La liste des vins pour la navigation et l'index du vin actuel dans cette liste
    private List<WineDto> wineList;
    private int currentIndex = -1;

    /**
     * Retourne le vin actuel.
     * @return Le vin actuel.
     */
    public LiveData<WineDto> getCurrentWine() {
        return currentWine;
    }

    /**
     * Définit la liste des vins.
     * @param list La liste des vins.
     */
    public void setWineList(List<WineDto> list) {
        this.wineList = list;
    }

    /**
     * Définit le vin actuel.
     * @param wine Le vin à définir.
     */
    public void setCurrentWine(WineDto wine) {
        isFavorite.setValue(false);

        if (wine != null) {
            SessionManager.getInstance().addWineToHistory(wine.getId());
            checkIfFavorite(wine);

            if (wineList != null) {
                currentIndex = -1;
                for (int i = 0; i < wineList.size(); i++) {
                    if (wineList.get(i).getId() == wine.getId() ||
                            (wineList.get(i).getTitle() != null &&
                                    wineList.get(i).getTitle().equals(wine.getTitle()))) {
                        currentIndex = i;
                        break;
                    }
                }
            }

            TranslationManager.getInstance().translateWineIfNeeded(wine, translatedWine -> {
                currentWine.setValue(translatedWine);
            });
        } else {
            currentWine.setValue(null);
        }
    }

    /**
     * Vérifie si le vin est dans les favoris.
     */
    private void checkIfFavorite(WineDto wine) {
        LiveData<UiState<List<WineDto>>> source = Repository.getInstance().getFavorites();
        source.observeForever(new Observer<UiState<List<WineDto>>>() {
            @Override
            public void onChanged(UiState<List<WineDto>> state) {
                if (!state.isLoading()) {
                    source.removeObserver(this);
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

    /**
     * Passe au vin suivant dans la liste.
     * @return true si le vin a été changé, false sinon.
     */
    public boolean nextWine() {
        if (wineList != null && currentIndex >= 0 && currentIndex < wineList.size() - 1) {
            currentIndex++;
            setCurrentWine(wineList.get(currentIndex));
            return true;
        }
        return false;
    }

    /**
     * Retourne l'état de favori pour le vin actuel.
     */
    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }

    /**
     * Bascule l'état de favori pour le vin actuel.
     */
    public void toggleFavorite() {
        WineDto wine = currentWine.getValue();
        if (wine == null) return;

        Boolean current = isFavorite.getValue();
        boolean isFav = current != null ? current : false;

        isFavorite.setValue(!isFav);

        if (isFav) {
            Repository.getInstance().removeFavorite(wine.getId());
        } else {
            Repository.getInstance().addFavorite(wine.getId());
        }
    }

}