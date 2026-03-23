package com.sipandsavour.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sipandsavour.R;
import com.sipandsavour.util.HapticUtil;

import java.util.ArrayList;
import java.util.List;

public class PreferencesFragment extends Fragment {

    private PreferencesViewModel viewModel;
    private NavController navController;

    private ChipGroup cgColor;
    private ChipGroup cgFlavors;
    private MaterialButton btnSave;

    // Classe interne pour lier l'affichage (FR) et la clé API (EN)
    private static class FlavorOption {
        String displayName;
        String apiKey;

        FlavorOption(String displayName, String apiKey) {
            this.displayName = displayName;
            this.apiKey = apiKey;
        }
    }

    // La liste complète de vos arômes de la recherche avancée
    private final List<FlavorOption> availableFlavors = new ArrayList<FlavorOption>() {{
        add(new FlavorOption("Fruité", "Fruity"));
        add(new FlavorOption("Boisé", "Woody"));
        add(new FlavorOption("Agrumes", "Citrus"));
        add(new FlavorOption("Floral", "Floral"));
        add(new FlavorOption("Épicé", "Spicy"));
        add(new FlavorOption("Terrestre", "Earthy"));
        add(new FlavorOption("Minéral", "Mineral"));
        add(new FlavorOption("Végétal / Herbacé", "Vegetal"));
        add(new FlavorOption("Beurré / Vanillé", "Buttery"));
        add(new FlavorOption("Fumé", "Smoky"));
        add(new FlavorOption("Caramel / Torréfié", "Caramel"));
        // N'hésitez pas à en ajouter d'autres ici pour coller exactement à votre API !
    }};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preferences, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);
        viewModel = new ViewModelProvider(this).get(PreferencesViewModel.class);

        cgColor = view.findViewById(R.id.cgColor);
        cgFlavors = view.findViewById(R.id.cgFlavors);
        btnSave = view.findViewById(R.id.btnSavePreferences);

        setupColorChips();
        setupFlavorChips();
        observeViewModel();

        btnSave.setOnClickListener(v -> {
            HapticUtil.playConfirm(v);
            viewModel.save();
            Toast.makeText(requireContext(), "Préférences sauvegardées !", Toast.LENGTH_SHORT).show();
            navController.popBackStack(); // Retourne au profil
        });
    }

    private void setupColorChips() {
        cgColor.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipRed) viewModel.setColor("Red");
            else if (checkedId == R.id.chipWhite) viewModel.setColor("White");
            else if (checkedId == R.id.chipRose) viewModel.setColor("Rose");
            else viewModel.setColor(null);
        });
    }

    private void setupFlavorChips() {
        for (FlavorOption flavor : availableFlavors) {
            Chip chip = new Chip(requireContext());
            chip.setText(flavor.displayName); // On affiche "Agrumes"
            chip.setTag(flavor.apiKey);       // On cache "Citrus" dans le Tag du Chip
            chip.setCheckable(true);

            // Applique le style par défaut des chips de sélection
            chip.setChipBackgroundColorResource(R.color.background);

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Quand on clique, on envoie "Citrus" (la clé API) au ViewModel
                viewModel.toggleFeature((String) buttonView.getTag());
            });
            cgFlavors.addView(chip);
        }
    }

    private void observeViewModel() {
        // Observer pour la couleur
        viewModel.getSelectedColor().observe(getViewLifecycleOwner(), color -> {
            if (color == null) {
                cgColor.clearCheck();
            } else {
                switch (color) {
                    case "Red": cgColor.check(R.id.chipRed); break;
                    case "White": cgColor.check(R.id.chipWhite); break;
                    case "Rose": cgColor.check(R.id.chipRose); break;
                }
            }
        });

        // Observer pour les arômes
        viewModel.getSelectedFeatures().observe(getViewLifecycleOwner(), features -> {
            if (features == null) return;

            // On parcourt tous les chips pour cocher ceux qui sont dans les préférences
            for (int i = 0; i < cgFlavors.getChildCount(); i++) {
                Chip chip = (Chip) cgFlavors.getChildAt(i);
                String apiKey = (String) chip.getTag();

                // On met à jour l'état sans déclencher le listener (pour éviter les boucles infinies)
                chip.setOnCheckedChangeListener(null);
                chip.setChecked(features.contains(apiKey));
                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    viewModel.toggleFeature((String) buttonView.getTag());
                });
            }
        });
    }
}