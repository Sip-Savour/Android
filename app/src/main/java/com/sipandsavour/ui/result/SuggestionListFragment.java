package com.sipandsavour.ui.result;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.sipandsavour.data.dto.BottleResponse; // Vérifiez que cet import correspond bien à votre classe
import com.sipandsavour.ui.selection.SelectionViewModel;

import java.util.ArrayList;
import java.util.List;

public class SuggestionListFragment extends Fragment implements SuggestionAdapter.OnSuggestionClickListener {

    private NavController navController;
    private ResultViewModel resultViewModel;
    private SelectionViewModel selectionViewModel; // <-- AJOUT DU VIEWMODEL DE SÉLECTION

    private RecyclerView rvSuggestions;
    private LinearLayout layoutEmpty;

    private SuggestionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suggestion_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);

        // IMPORTANT : On utilise requireActivity() pour récupérer la MÊME instance du ViewModel
        // que celle utilisée dans FlavorFragment !
        resultViewModel = new ViewModelProvider(requireActivity()).get(ResultViewModel.class);
        selectionViewModel = new ViewModelProvider(requireActivity()).get(SelectionViewModel.class);

        bindViews(view);
        setupRecyclerView();
        observePrediction(); // <-- On lance l'écoute de l'API
    }

    private void bindViews(View view) {
        rvSuggestions = view.findViewById(R.id.rvSuggestions);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
    }

    private void setupRecyclerView() {
        adapter = new SuggestionAdapter();
        adapter.setOnSuggestionClickListener(this);
        rvSuggestions.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSuggestions.setAdapter(adapter);
    }

    private void observePrediction() {
        // On écoute le résultat de l'API en direct
        selectionViewModel.getPredictionResult().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            if (state.isLoading()) {
                // Optionnel : Afficher un ProgressBar ici si vous en ajoutez un dans layoutEmpty
                rvSuggestions.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
            }
            else if (state.isSuccess() && state.getData() != null) {
                // Succès : On convertit les réponses de l'API en objets WineDto pour la liste
                List<BottleResponse> apiBottles = state.getData().getBottle();
                List<WineDto> suggestions = mapApiToWineDto(apiBottles);
                displaySuggestions(suggestions);
            }
            else if (state.isError()) {
                // Erreur : On affiche l'écran vide et un message
                rvSuggestions.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), "Erreur : " + state.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private List<WineDto> mapApiToWineDto(List<BottleResponse> apiBottles) {
        List<WineDto> list = new ArrayList<>();
        if (apiBottles == null) return list;

        int idCounter = 1; // WineDto a besoin d'un ID
        for (BottleResponse bottle : apiBottles) {

            // --- C'EST ICI QUE L'ON INVERSE LES DEUX PARAMÈTRES ---
            WineDto wine = new WineDto(
                    idCounter++,
                    bottle.getTitle(),
                    bottle.getDescription(), // <-- La description en premier
                    bottle.getVariety()      // <-- Le cépage (Variety) ensuite
            );

            wine.setColor(bottle.getColor());
            list.add(wine);
        }
        return list;
    }

    private void displaySuggestions(List<WineDto> suggestions) {
        if (suggestions == null || suggestions.isEmpty()) {
            rvSuggestions.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvSuggestions.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            adapter.submitList(suggestions);
            resultViewModel.setWineList(suggestions);
        }
    }

    @Override
    public void onSuggestionClick(WineDto wine) {
        resultViewModel.setCurrentWine(wine);
        navController.navigate(R.id.action_suggestion_to_detail);
    }
}