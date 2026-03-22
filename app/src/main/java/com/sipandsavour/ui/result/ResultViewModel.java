package com.sipandsavour.ui.result;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sipandsavour.data.dto.WineDto;
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

        if (wineList != null && wine != null) {
            currentIndex = -1;
            // DOUBLE SÉCURITÉ : Recherche par ID ou par Titre
            for (int i = 0; i < wineList.size(); i++) {
                if (wineList.get(i).getId() == wine.getId() ||
                        (wineList.get(i).getTitle() != null && wineList.get(i).getTitle().equals(wine.getTitle()))) {
                    currentIndex = i;
                    break;
                }
            }
        }
    }

    public boolean nextWine() {
        if (wineList != null && currentIndex >= 0 && currentIndex < wineList.size() - 1) {
            currentIndex++;
            currentWine.setValue(wineList.get(currentIndex));
            return true;
        }
        return false;
    }

    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }

    public void toggleFavorite() {
        Boolean current = isFavorite.getValue();
        isFavorite.setValue(current != null ? !current : true);
    }
}