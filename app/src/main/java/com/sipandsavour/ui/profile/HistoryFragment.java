package com.sipandsavour.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.sipandsavour.R;
import com.sipandsavour.data.dto.WineDto;
import com.sipandsavour.ui.favorites.FavoritesAdapter;
import com.sipandsavour.ui.profile.HistoryViewModel;
import com.sipandsavour.ui.result.ResultViewModel;
import com.sipandsavour.util.SlideBackUtil;

public class HistoryFragment extends Fragment implements FavoritesAdapter.OnFavoriteClickListener {

    private HistoryViewModel viewModel;
    private ResultViewModel resultViewModel;
    private NavController navController;

    private FavoritesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);
        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        resultViewModel = new ViewModelProvider(requireActivity()).get(ResultViewModel.class);
        androidx.recyclerview.widget.RecyclerView rvHistory = view.findViewById(R.id.rvHistory);

        View layoutEmptyHistory = view.findViewById(R.id.layoutEmptyHistory);
        View progressHistory = view.findViewById(R.id.progressHistory);

        adapter = new FavoritesAdapter(); // On recycle l'adapter des favoris !
        adapter.setOnFavoriteClickListener(this);
        rvHistory.setAdapter(adapter);

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading ->
                progressHistory.setVisibility(loading ? View.VISIBLE : View.GONE));

        viewModel.getHistoryList().observe(getViewLifecycleOwner(), wines -> {
            adapter.submitList(wines);
            layoutEmptyHistory.setVisibility(wines.isEmpty() ? View.VISIBLE : View.GONE);
            rvHistory.setVisibility(wines.isEmpty() ? View.GONE : View.VISIBLE);
        });

        SlideBackUtil.attach(() -> navController.popBackStack(), view,rvHistory);
        viewModel.loadHistory();
    }

    @Override
    public void onFavoriteClick(WineDto wine, int position) {
        // Redirige vers la page du vin quand on clique dessus
        resultViewModel.setCurrentWine(wine);
        navController.navigate(R.id.action_history_to_wineDetail);
    }
}