package com.sipandsavour.ui.selection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sipandsavour.data.dto.meal.MealDto;
import com.sipandsavour.util.HapticUtil;
import com.sipandsavour.util.MealTranslationManager;

import java.util.List;

public class FlavorFragment extends Fragment implements
        CategoryAdapter.OnFlavorSelectionListener,
        CategoryAdapter.OnCategoryClickListener {

    private SelectionViewModel viewModel;
    private NavController navController;

    private TextView tvHeaderTitle;
    private RecyclerView rvAccordion;
    private RecyclerView rvMealSuggestions;
    private MaterialButton btnMatch;

    private CategoryAdapter categoryAdapter;
    private MealSuggestionAdapter mealSuggestionAdapter;
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
        viewModel.clearSelections();

        tvHeaderTitle = view.findViewById(R.id.tvHeaderTitle);
        rvAccordion = view.findViewById(R.id.rvAccordion);
        rvMealSuggestions = view.findViewById(R.id.rvMealSuggestions);
        btnMatch = view.findViewById(R.id.btnMatch);

        setupHeader();
        setupRecyclerView();
        setupButton();
        observeViewModel();
    }

    private void setupHeader() {
        if (tvHeaderTitle != null) {
            tvHeaderTitle.setText(mode.equals("match")
                    ? R.string.flavor_title
                    : R.string.flavor_title_search);
        }
    }

    private void setupRecyclerView() {
        categoryAdapter = new CategoryAdapter();
        categoryAdapter.setOnFlavorSelectionListener(this);
        categoryAdapter.setOnCategoryClickListener(this);

        rvAccordion.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvAccordion.setAdapter(categoryAdapter);
        rvAccordion.setItemAnimator(null);

        // Setup meal suggestions RecyclerView
        mealSuggestionAdapter = new MealSuggestionAdapter();
        mealSuggestionAdapter.setOnMealClickListener(meal -> {
            // Afficher les détails de la recette dans un bottom sheet
            showMealDetailsBottomSheet(meal);
        });

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        rvMealSuggestions.setLayoutManager(horizontalLayoutManager);
        rvMealSuggestions.setAdapter(mealSuggestionAdapter);

        // Load meal suggestions
        loadMealSuggestions();
    }

    private void setupButton() {
        btnMatch.setText(mode.equals("match")
                ? R.string.search_match_button
                : R.string.search_filter_button);

        btnMatch.setOnClickListener(v -> {
            HapticUtil.playConfirm(v);
            onMatchClicked();
        });
    }

    private void observeViewModel() {
        viewModel.getCategories().observe(getViewLifecycleOwner(),
                categories -> categoryAdapter.setCategories(categories));

        viewModel.getSelectedFlavors().observe(getViewLifecycleOwner(), selectedFlavors -> {
            // Le bouton s'active si l'on a sélectionné au moins une saveur OU une couleur
            boolean hasSelection = viewModel.hasSelection();
            btnMatch.setEnabled(hasSelection);
            categoryAdapter.updateSelectedFlavors(selectedFlavors);
        });
    }

    private void onMatchClicked() {
        if (!viewModel.hasSelection()) {
            showSnackbar(getString(R.string.error_unknown));
            return;
        }
        viewModel.predict();

        // Naviguer vers la liste de suggestions
        // L'action dépend de la destination actuelle dans le nav_graph
        int currentDestId = navController.getCurrentDestination() != null
                ? navController.getCurrentDestination().getId() : 0;

        if (currentDestId == R.id.advancedSearchFragment) {
            navController.navigate(R.id.action_search_to_suggestions);
        } else if (currentDestId == R.id.flavorFromMealFragment) {
            navController.navigate(R.id.action_flavorMeal_to_suggestions);
        } else if (currentDestId == R.id.flavorSelectionFragment) {
            navController.navigate(R.id.action_flavor_to_suggestions);
        }
    }

    private void showSnackbar(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }

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

    /**
     * Charge les suggestions de repas depuis l'API et les traduit
     */
    private void loadMealSuggestions() {
        viewModel.getMealSuggestions().observe(getViewLifecycleOwner(), state -> {
            if (state.isSuccess() && state.getData() != null && state.getData().getMeals() != null) {
                List<MealDto> meals = state.getData().getMeals();
                
                // Traduire les repas avant d'afficher
                translateAndDisplayMeals(meals);
            }
        });
    }

    /**
     * Traduit les recettes puis les affiche dans l'adapter
     */
    private void translateAndDisplayMeals(List<MealDto> meals) {
        if (meals.isEmpty()) {
            mealSuggestionAdapter.setMeals(meals);
            return;
        }

        // Compter les recettes à traduire
        int[] completedCount = {0};
        final int totalMeals = meals.size();

        for (MealDto meal : meals) {
            MealTranslationManager.getInstance().translateMealIfNeeded(meal, translatedMeal -> {
                completedCount[0]++;
                if (completedCount[0] == totalMeals) {
                    // Toutes les traductions sont terminées, afficher l'adapter
                    mealSuggestionAdapter.setMeals(meals);
                }
            });
        }
    }

    /**
     * Affiche les détails complets d'une recette dans un bottom sheet
     */
    private void showMealDetailsBottomSheet(MealDto meal) {
        // Récupérer les détails complets de la recette (avec ingrédients et instructions)
        viewModel.getMealDetails(meal.getIdMeal()).observe(getViewLifecycleOwner(), state -> {
            if (state.isSuccess() && state.getData() != null && !state.getData().getMeals().isEmpty()) {
                MealDto fullMeal = state.getData().getMeals().get(0);
                MealDetailsBottomSheetFragment bottomSheet = MealDetailsBottomSheetFragment.newInstance(fullMeal);
                bottomSheet.show(getChildFragmentManager(), "MealDetails");
            }
        });
    }
}