package com.sipandsavour.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sipandsavour.data.SessionManager;
import com.sipandsavour.data.api.ApiClient;
import com.sipandsavour.data.dto.WineDto;
import com.sipandsavour.util.TranslationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class HistoryViewModel extends ViewModel {

    private final MutableLiveData<List<WineDto>> historyList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public LiveData<List<WineDto>> getHistoryList() { return historyList; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadHistory() {
        isLoading.setValue(true);
        List<Integer> ids = SessionManager.getInstance().getHistoryIds();

        if (ids.isEmpty()) {
            historyList.setValue(new ArrayList<>());
            isLoading.setValue(false);
            return;
        }

        executor.execute(() -> {
            List<WineDto> fetchedWines = new ArrayList<>();
            for (Integer id : ids) {
                try {
                    Response<WineDto> response = ApiClient.getInstance().getWineApi().getWineById(id).execute();
                    if (response.isSuccessful() && response.body() != null) {
                        fetchedWines.add(response.body());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Traduction de la liste d'historique complète avant affichage
            TranslationManager.getInstance().translateWineListIfNeeded(fetchedWines, translatedWines -> {
                historyList.postValue(translatedWines);
                isLoading.postValue(false);
            });
        });
    }
}