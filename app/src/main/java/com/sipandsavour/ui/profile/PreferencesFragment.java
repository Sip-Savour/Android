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
import com.sipandsavour.util.SlideBackUtil;

import java.util.ArrayList;
import java.util.List;

public class PreferencesFragment extends Fragment {

    private PreferencesViewModel viewModel;
    private NavController navController;

    private ChipGroup cgColor;
    private ChipGroup cgFlavors;
    private MaterialButton btnSave;

    private static class FlavorOption {
        String displayName;
        String apiKey;

        FlavorOption(String displayName, String apiKey) {
            this.displayName = displayName;
            this.apiKey = apiKey;
        }
    }

    // Méthode pour générer la liste dynamiquement avec la bonne langue
    private List<FlavorOption> getAvailableFlavors() {
        List<FlavorOption> flavors = new ArrayList<>();
        flavors.add(new FlavorOption(getString(R.string.flavor_fruity), "Fruity"));
        flavors.add(new FlavorOption(getString(R.string.flavor_woody), "Woody"));
        flavors.add(new FlavorOption(getString(R.string.flavor_citrus), "Citrus"));
        flavors.add(new FlavorOption(getString(R.string.flavor_floral), "Floral"));
        flavors.add(new FlavorOption(getString(R.string.flavor_spicy), "Spicy"));
        flavors.add(new FlavorOption(getString(R.string.flavor_earthy), "Earthy"));
        flavors.add(new FlavorOption(getString(R.string.flavor_mineral), "Mineral"));
        flavors.add(new FlavorOption(getString(R.string.flavor_vegetal), "Vegetal"));
        flavors.add(new FlavorOption(getString(R.string.flavor_buttery), "Buttery"));
        flavors.add(new FlavorOption(getString(R.string.flavor_smoked), "Smoky"));
        flavors.add(new FlavorOption(getString(R.string.flavor_caramel), "Caramel"));
        return flavors;
    }

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
            Toast.makeText(requireContext(), getString(R.string.preferences_saved_toast), Toast.LENGTH_SHORT).show();
            navController.popBackStack();
        });

        View scrollView = view.findViewById(R.id.scrollView);
        SlideBackUtil.attach(() -> navController.popBackStack(), view, scrollView);
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
        // Utilisation de la liste dynamique traduite
        for (FlavorOption flavor : getAvailableFlavors()) {
            Chip chip = new Chip(requireContext());
            chip.setText(flavor.displayName);
            chip.setTag(flavor.apiKey);
            chip.setCheckable(true);

            chip.setChipBackgroundColorResource(R.color.background);

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                viewModel.toggleFeature((String) buttonView.getTag());
            });
            cgFlavors.addView(chip);
        }
    }

    private void observeViewModel() {
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

        viewModel.getSelectedFeatures().observe(getViewLifecycleOwner(), features -> {
            if (features == null) return;

            for (int i = 0; i < cgFlavors.getChildCount(); i++) {
                Chip chip = (Chip) cgFlavors.getChildAt(i);
                String apiKey = (String) chip.getTag();

                chip.setOnCheckedChangeListener(null);
                chip.setChecked(features.contains(apiKey));
                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    viewModel.toggleFeature((String) buttonView.getTag());
                });
            }
        });
    }
}