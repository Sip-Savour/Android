package com.sipandsavour.ui.selection;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.sipandsavour.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MealFragment extends Fragment {

    private NavController navController;
    private SelectionViewModel viewModel;

    private RecyclerView rvBaseOptions;
    private RecyclerView rvTasteOptions;
    private RecyclerView rvColorOptions; // NOUVEAU
    private MaterialButton btnFindWine;

    private MealCardAdapter baseAdapter;
    private MealCardAdapter tasteAdapter;
    private MealCardAdapter colorAdapter; // NOUVEAU

    // Les options affichées sur les cartes
    private final List<String> baseOptions = Arrays.asList(
            "Viande Rouge", "Viande Blanche", "Volaille", "Poisson", "Fruits de mer", "Végétarien", "Fromage"
    );
    private final List<String> tasteOptions = Arrays.asList(
            "Gras", "Riche", "Sec", "Salé", "Sucré", "Poivré", "Épicé", "Acide"
    );
    private final List<String> colorOptions = Arrays.asList(
            "Vin Rouge", "Vin Blanc", "Vin Rosé"
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);
        viewModel = new ViewModelProvider(requireActivity()).get(SelectionViewModel.class);

        rvBaseOptions = view.findViewById(R.id.rvBaseOptions);
        rvTasteOptions = view.findViewById(R.id.rvTasteOptions);
        rvColorOptions = view.findViewById(R.id.rvColorOptions);
        btnFindWine = view.findViewById(R.id.btnFindWine);

        setupRecyclerViews();
        setupButton();
    }

    private void setupRecyclerViews() {
        // 1. Grille pour la Base du plat (2 colonnes, choix unique)
        baseAdapter = new MealCardAdapter(baseOptions, true, selectedItems -> {
            checkIfCanFindWine();
        });
        rvBaseOptions.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvBaseOptions.setAdapter(baseAdapter);

        // 2. Grille pour les Saveurs (3 colonnes, choix multiples)
        tasteAdapter = new MealCardAdapter(tasteOptions, false, selectedItems -> {
            checkIfCanFindWine();
        });
        rvTasteOptions.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        rvTasteOptions.setAdapter(tasteAdapter);

        // 3. Grille pour la Couleur (3 colonnes, choix unique)
        colorAdapter = new MealCardAdapter(colorOptions, true, selectedItems -> {
            checkIfCanFindWine();
        });
        rvColorOptions.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        rvColorOptions.setAdapter(colorAdapter);
    }

    private void checkIfCanFindWine() {
        // Le bouton s'active si la base est sélectionnée (la couleur est optionnelle)
        boolean hasBase = !baseAdapter.getSelectedItems().isEmpty();
        btnFindWine.setEnabled(hasBase);
    }

    private void setupButton() {
        btnFindWine.setOnClickListener(v -> {
            // On rassemble TOUS les choix
            Set<String> allChoices = new HashSet<>();
            allChoices.addAll(baseAdapter.getSelectedItems());
            allChoices.addAll(tasteAdapter.getSelectedItems());
            allChoices.addAll(colorAdapter.getSelectedItems()); // On ajoute la couleur

            // 1. On lance la traduction intelligente (Plat -> Vin)
            viewModel.predictFromMeal(allChoices);

            // 2. Navigation
            navController.navigate(R.id.action_meal_to_suggestions);
        });
    }
}