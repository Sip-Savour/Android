package com.sipandsavour.ui.result;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sipandsavour.R;
import com.sipandsavour.data.dto.WineDto;
import com.sipandsavour.util.SlideBackUtil;

import java.util.List;

public class RandomWinesFragment extends Fragment implements SuggestionAdapter.OnSuggestionClickListener {

    private NavController navController;
    private ResultViewModel resultViewModel;
    private RandomViewModel randomViewModel;

    private RecyclerView rvRandomWines;
    private ProgressBar progressRandom;
    private SuggestionAdapter adapter;

    @Nullable
    @Override
    /**
     * Inflate le layout du fragment.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_random_wines, container, false);
    }

    @Override
    /**
     * Appelé après que la vue du fragment soit créée.
     */
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);
        resultViewModel = new ViewModelProvider(requireActivity()).get(ResultViewModel.class);
        randomViewModel = new ViewModelProvider(this).get(RandomViewModel.class);

        rvRandomWines = view.findViewById(R.id.rvRandomWines);
        progressRandom = view.findViewById(R.id.progressRandom);

        setupRecyclerView();
        observeViewModel();

        SlideBackUtil.attach(() -> navController.popBackStack(), view, rvRandomWines);

        // Lancement automatique de la recherche des 5 vins à l'ouverture de la page
        randomViewModel.loadRandomWines();
    }

    /**
     * Configure le RecyclerView pour afficher les vins aléatoires.
     */
    private void setupRecyclerView() {
        adapter = new SuggestionAdapter();
        adapter.setOnSuggestionClickListener(this);
        rvRandomWines.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRandomWines.setAdapter(adapter);
    }

    /**
     * Surveille les changements dans le ViewModel.
     */
    private void observeViewModel() {
        randomViewModel.getRandomWinesState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            if (state.isLoading()) {
                rvRandomWines.setVisibility(View.GONE);
                progressRandom.setVisibility(View.VISIBLE);
            }
            else if (state.isSuccess() && state.getData() != null) {
                progressRandom.setVisibility(View.GONE);
                rvRandomWines.setVisibility(View.VISIBLE);

                List<WineDto> randomWines = state.getData();
                adapter.submitList(randomWines);
                resultViewModel.setWineList(randomWines);
            }
            else if (state.isError()) {
                progressRandom.setVisibility(View.GONE);

                if (getContext() != null && isAdded()) {
                    Toast.makeText(getContext(), getString(R.string.error_title) + " " + state.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    /**
     * Appelé lorsqu'un utilisateur clique sur une suggestion de vin.
     * @param wine Le vin sélectionné.
     */
    public void onSuggestionClick(WineDto wine) {
        resultViewModel.setCurrentWine(wine);
        navController.navigate(R.id.action_random_to_detail);
    }
}