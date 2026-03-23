package com.sipandsavour.ui.result;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.sipandsavour.R;
import com.sipandsavour.data.dto.WineDto;

public class WeeklyChoiceFragment extends Fragment {

    // Views
    private TextView tvCepage;
    private TextView tvDescription;
    private TextView tvType;
    private TextView tvMealName;
    private TextView tvIngredients;

    private NavController navController;
    private WeeklyChoiceViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weekly_choice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);

        // Initialisation du ViewModel
        viewModel = new ViewModelProvider(this).get(WeeklyChoiceViewModel.class);

        bindViews(view);
        observeViewModel();

        // Lancement du chargement de la recommandation dès l'ouverture de l'écran
        viewModel.loadRecommendation();
    }

    private void bindViews(View view) {
        tvCepage = view.findViewById(R.id.tvCepage);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvType = view.findViewById(R.id.tvType);
        tvMealName = view.findViewById(R.id.tvMealName);
        tvIngredients = view.findViewById(R.id.tvIngredients);
    }

    private void observeViewModel() {
        viewModel.getRecommendationState().observe(getViewLifecycleOwner(), state -> {
            if (state.isLoading()) {
                // Pendant la recherche du vin, on affiche un texte d'attente
                if (tvCepage != null) tvCepage.setText("Recherche de la meilleure bouteille...");
                if (tvDescription != null) tvDescription.setText("Nous interrogeons notre sommelier virtuel pour vous...");
                if (tvType != null) tvType.setText("");
            } else if (state.isSuccess() && state.getData() != null) {
                // Succès : on affiche les vraies données
                displayWine(state.getData());
            } else if (state.isError()) {
                // Erreur : on prévient l'utilisateur
                Toast.makeText(requireContext(), state.getMessage(), Toast.LENGTH_LONG).show();
                if (tvCepage != null) tvCepage.setText("Oups !");
                if (tvDescription != null) tvDescription.setText(state.getMessage());
            }
        });
    }

    private void displayWine(WineDto wine) {
        // On remplace les données de test par le vin suggéré
        if (tvCepage != null) tvCepage.setText(wine.getTitle() != null ? wine.getTitle() : (wine.getVariety() != null ? wine.getVariety() : "Cépage inconnu"));
        if (tvDescription != null) tvDescription.setText(wine.getDescription() != null ? wine.getDescription() : "Aucune description disponible pour ce vin.");
        if (tvType != null) tvType.setText(wine.getColorDisplayName());

        // Note : N'ayant pas encore les recettes associées au vin dans l'API,
        // on peut mettre un texte générique en attendant, ou les cacher.
        if (tvMealName != null) tvMealName.setText("Accords Mets-Vins");
        if (tvIngredients != null) tvIngredients.setText("Bientôt disponible...");
    }
}