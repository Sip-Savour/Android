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

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.sipandsavour.R;
import com.sipandsavour.data.dto.WineDto;
import com.sipandsavour.data.dto.BottleResponse; // Vérifiez que cet import correspond bien à votre classe
import com.sipandsavour.ui.selection.SelectionViewModel;
import com.sipandsavour.util.SlideBackUtil;

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
        observePrediction();
        SlideBackUtil.attach(() -> navController.popBackStack(), view, rvSuggestions);
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
                triggerSuccessVibration();
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

        for (BottleResponse bottle : apiBottles) {
            // --- CORRECTION : ON UTILISE L'ID ET LA COULEUR DE L'API ---
            WineDto wine = new WineDto(
                    bottle.getId(),          // <-- L'ID renvoyé par l'API
                    bottle.getTitle(),       // <-- Titre
                    bottle.getDescription(), // <-- Description
                    bottle.getVariety(),     // <-- Cépage
                    bottle.getColor()        // <-- Couleur (5ème paramètre requis par WineDto)
            );

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

    private void triggerSuccessVibration() {
        Vibrator vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            // Une vibration très courte et douce de 50 millisecondes
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                // Pour les anciens téléphones
                vibrator.vibrate(50);
            }
        }
    }


    @Override
    public void onSuggestionClick(WineDto wine) {
        resultViewModel.setCurrentWine(wine);
        navController.navigate(R.id.action_suggestion_to_detail);
    }
}