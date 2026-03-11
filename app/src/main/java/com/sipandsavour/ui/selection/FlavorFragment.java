package com.sipandsavour.ui.selection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.sipandsavour.R;

/**
 * Fragment de sélection des saveurs avec accordéon.
 */
public class FlavorFragment extends Fragment implements
        CategoryAdapter.OnFlavorSelectionListener,
        CategoryAdapter.OnCategoryClickListener {

    private SelectionViewModel viewModel;
    private NavController navController;

    // Views
    private ImageButton btnBack;
    private TextView tvHeaderTitle;
    private RecyclerView rvAccordion;
    private MaterialButton btnMatch;

    // Adapter
    private CategoryAdapter categoryAdapter;

    // Mode
    private String mode = "match";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = getArguments().getString("mode", "match");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_flavor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);
        viewModel = new ViewModelProvider(requireActivity()).get(SelectionViewModel.class);
        viewModel.setMode(mode);

        bindViews(view);
        setupHeader();
        setupRecyclerView();
        setupButton();
        observeViewModel();
    }

    private void bindViews(View view) {
        View headerLayout = view.findViewById(R.id.appBarLayout);
        if (headerLayout != null) {
            btnBack = headerLayout.findViewById(R.id.btnBack);
            tvHeaderTitle = headerLayout.findViewById(R.id.tvHeaderTitle);
        }

        rvAccordion = view.findViewById(R.id.rvAccordion);
        btnMatch = view.findViewById(R.id.btnMatch);
    }

    private void setupHeader() {
        if (tvHeaderTitle != null) {
            tvHeaderTitle.setText(mode.equals("match")
                    ? R.string.flavor_title_match
                    : R.string.flavor_title_search);
        }

        if (btnBack != null) {
            btnBack.setVisibility(View.VISIBLE);
            btnBack.setOnClickListener(v -> navController.navigateUp());
        }
    }

    private void setupRecyclerView() {
        categoryAdapter = new CategoryAdapter();
        categoryAdapter.setOnFlavorSelectionListener(this);
        categoryAdapter.setOnCategoryClickListener(this);

        rvAccordion.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvAccordion.setAdapter(categoryAdapter);
        rvAccordion.setItemAnimator(null);
    }

    private void setupButton() {
        btnMatch.setText(mode.equals("match")
                ? R.string.search_match_button
                : R.string.search_filter_button);

        btnMatch.setOnClickListener(v -> onMatchClicked());
    }

    private void observeViewModel() {
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            categoryAdapter.setCategories(categories);
        });

        viewModel.getSelectedFlavors().observe(getViewLifecycleOwner(), selectedFlavors -> {
            boolean hasSelection = !selectedFlavors.isEmpty();
            btnMatch.setEnabled(hasSelection);
            categoryAdapter.updateSelectedFlavors(selectedFlavors);
        });
    }

    private void onMatchClicked() {
        if (!viewModel.hasSelection()) {
            showSnackbar(getString(R.string.error_unknown));
            return;
        }

        // TODO: Appeler viewModel.predict()
        // TODO: Observer le résultat et naviguer vers les résultats

        // Navigation temporaire
        if (mode.equals("match")) {
            navController.navigate(R.id.action_flavor_to_loading);
        } else {
            navController.navigate(R.id.action_search_to_loading);
        }
    }

    private void showSnackbar(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }

    // === CALLBACKS ===

    @Override
    public void onFlavorToggled(String flavorKey) {
        viewModel.toggleFlavor(flavorKey);
    }

    @Override
    public boolean isFlavorSelected(String flavorKey) {
        return viewModel.isFlavorSelected(flavorKey);
    }

    @Override
    public void onCategoryToggled(int position) {
        viewModel.toggleCategory(position);
    }
}