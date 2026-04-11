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

import com.google.android.material.card.MaterialCardView;
import com.sipandsavour.R;
import com.sipandsavour.data.Repository;
import com.sipandsavour.data.dto.WineDto;
import com.sipandsavour.data.dto.meal.MealDto;
import com.sipandsavour.ui.selection.MealDetailsBottomSheetFragment;
import com.sipandsavour.util.HapticUtil;
import com.sipandsavour.util.SlideBackUtil;

import java.util.List;

public class WeeklyChoiceFragment extends Fragment {

    // Views Vin
    private MaterialCardView cardWine;
    private TextView tvCepage;
    private TextView tvDescription;
    private TextView tvType;

    // Views Plat
    private MaterialCardView cardMeal;
    private TextView tvMealName;
    private TextView tvIngredients;

    private NavController navController;
    private WeeklyChoiceViewModel viewModel;

    // Données actuelles pour les clics
    private WineDto currentWine;
    private MealDto currentMeal;

    @Nullable
    @Override
    /**
     * Inflate le layout du fragment
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weekly_choice, container, false);
    }

    @Override
    /**
     * Initialize views and set up the fragment
     */
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);
        viewModel = new ViewModelProvider(this).get(WeeklyChoiceViewModel.class);

        bindViews(view);
        setupListeners();
        observeViewModel();

        viewModel.loadRecommendation();

        View scrollView = view.findViewById(R.id.nestedScrollView);
        SlideBackUtil.attach(() -> navController.popBackStack(), view, scrollView);
    }

    /**
     * Bind the views to the fragment.
     * @param view The root view of the fragment.
     */
    private void bindViews(View view) {
        cardWine = view.findViewById(R.id.cardWine);
        tvCepage = view.findViewById(R.id.tvCepage);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvType = view.findViewById(R.id.tvType);

        cardMeal = view.findViewById(R.id.cardMeal);
        tvMealName = view.findViewById(R.id.tvMealName);
        tvIngredients = view.findViewById(R.id.tvIngredients);
    }

    /**
     * Set up the click listeners for the views.
     */
    private void setupListeners() {
        // Clic sur la carte du vin
        if (cardWine != null) {
            cardWine.setOnClickListener(v -> {
                HapticUtil.playLightClick(v);
                openWineDetails();
            });
        }

        // Clic sur la carte du plat
        if (cardMeal != null) {
            cardMeal.setOnClickListener(v -> {
                HapticUtil.playLightClick(v);
                openMealDetails();
            });
        }
    }

    /**
     * Open the details for the selected wine.
     */
    private void openWineDetails() {
        if (currentWine == null) {
            Toast.makeText(requireContext(), getString(R.string.loading), Toast.LENGTH_SHORT).show();
            return;
        }

        WineDetailsBottomSheetFragment bottomSheet = WineDetailsBottomSheetFragment.newInstance(currentWine);
        bottomSheet.show(getParentFragmentManager(), "WineDetails");
    }

    /**
     * Open the details for the selected meal.
     */
    private void openMealDetails() {
        if (currentMeal == null) {
            Toast.makeText(requireContext(), getString(R.string.loading), Toast.LENGTH_SHORT).show();
            return;
        }

        MealDetailsBottomSheetFragment bottomSheet = MealDetailsBottomSheetFragment.newInstance(currentMeal);
        bottomSheet.show(getParentFragmentManager(), "MealDetails");
    }

    /**
     * Observe the ViewModel for updates.
     */
    private void observeViewModel() {
        // Observer le résultat principal
        viewModel.getPairingState().observe(getViewLifecycleOwner(), state -> {
            if (state.isLoading()) {
                showLoading();
            } else if (state.isSuccess() && state.getData() != null) {
                Repository.WeeklyPairingResult result = state.getData();
                currentWine = result.getWine();
                currentMeal = result.getMeal();
                displayWine(currentWine);
                displayMeal(currentMeal);
            } else if (state.isError()) {
                showError(state.getMessage());
            }
        });

        // Observer le plat traduit
        viewModel.getTranslatedMeal().observe(getViewLifecycleOwner(), translatedMeal -> {
            if (translatedMeal != null) {
                currentMeal = translatedMeal;
                displayMealTranslated(translatedMeal);
            }
        });
    }

    /**
     * Show the loading state.
     */
    private void showLoading() {
        if (tvCepage != null) tvCepage.setText(getString(R.string.weekly_loading_title));
        if (tvDescription != null) tvDescription.setText(getString(R.string.weekly_loading_desc));
        if (tvType != null) tvType.setText("");
        if (tvMealName != null) tvMealName.setText(getString(R.string.loading));
        if (tvIngredients != null) tvIngredients.setText("");
    }

    /**
     * Show an error message.
     * @param message The error message to display.
     */
    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        if (tvCepage != null) tvCepage.setText(getString(R.string.error_title));
        if (tvDescription != null) tvDescription.setText(message);
    }

    /**
     * Display the details for the selected wine.
     * @param wine The wine to display.
     */
    private void displayWine(WineDto wine) {
        if (wine == null) return;

        if (tvCepage != null) {
            tvCepage.setText(wine.getTitle() != null ? wine.getTitle() :
                    (wine.getVariety() != null ? wine.getVariety() : getString(R.string.result_unknown_wine)));
        }

        if (tvDescription != null) {
            tvDescription.setText(wine.getDescription() != null ? wine.getDescription() : getString(R.string.result_no_description));
        }

        if (tvType != null) {
            tvType.setText(wine.getColorDisplayName());
        }
    }

    /**
     * Display the details for the selected meal.
     * @param meal The meal to display.
     */
    private void displayMeal(MealDto meal) {
        if (meal == null) {
            if (tvMealName != null) tvMealName.setText(getString(R.string.weekly_no_meal));
            if (tvIngredients != null) tvIngredients.setText("");
            return;
        }

        if (tvMealName != null) {
            tvMealName.setText(meal.getName() != null ? meal.getName() : meal.getStrMeal());
        }

        displayIngredients(meal);
    }

    /**
     * Display the translated details for the selected meal.
     * @param meal The meal to display.
     */
    private void displayMealTranslated(MealDto meal) {
        if (meal == null) return;

        if (tvMealName != null) {
            tvMealName.setText(meal.getName());
        }

        displayIngredients(meal);
    }

    /**
     * Display the ingredients for the selected meal.
     * @param meal The meal to display.
     */
    private void displayIngredients(MealDto meal) {
        if (tvIngredients == null) return;

        List<String> ingredients = meal.getIngredients();
        List<String> measures = meal.getMeasures();

        if (ingredients == null || ingredients.isEmpty()) {
            tvIngredients.setText(getString(R.string.weekly_no_ingredients));
            return;
        }

        StringBuilder sb = new StringBuilder();
        int max = Math.min(6, ingredients.size());

        for (int i = 0; i < max; i++) {
            String ingredient = ingredients.get(i);
            String measure = (measures != null && i < measures.size()) ? measures.get(i) : "";

            if (ingredient == null || ingredient.trim().isEmpty()) continue;

            if (sb.length() > 0) sb.append("\n");
            sb.append("• ");
            if (measure != null && !measure.trim().isEmpty()) {
                sb.append(measure.trim()).append(" ");
            }
            sb.append(ingredient.trim());
        }

        if (ingredients.size() > 6) {
            sb.append("\n+ ").append(ingredients.size() - 6).append(" autres...");
        }

        tvIngredients.setText(sb.toString());
    }
}