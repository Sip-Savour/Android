package com.sipandsavour.ui.result;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.sipandsavour.R;
import com.sipandsavour.data.Repository;
import com.sipandsavour.data.dto.WineDto;
import com.sipandsavour.data.dto.meal.MealDto;
import com.sipandsavour.ui.selection.MealDetailsBottomSheetFragment;
import com.sipandsavour.util.HapticUtil;
import com.sipandsavour.util.MealTranslationManager;
import com.sipandsavour.util.SlideBackUtil;
import com.sipandsavour.util.WineFoodPairingUtil;

import java.util.List;
import java.util.Random;

public class ResultFragment extends Fragment {

    private ResultViewModel viewModel;

    // Wine card views
    private TextView tvTitle;
    private TextView tvCepage;
    private TextView tvDescription;
    private TextView tvType;
    private ImageButton fabFavorite;

    private NavController navController;
    private LinearLayout layoutMealPairing;
    private TextView tvMealPairingName;
    private TextView tvMealPairingIngredients;

    // Données actuelles
    private MealDto currentPairedMeal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = NavHostFragment.findNavController(this);
        viewModel = new ViewModelProvider(requireActivity()).get(ResultViewModel.class);

        initViews(view);
        setupListeners();
        observeViewModel();
        handleArguments();

        // On utilise le bon ID du XML (scrollView)
        View scrollView = view.findViewById(R.id.scrollView);

        // Détecteur local spécifique au carrousel des résultats
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            private float startX = 0;
            private float startY = 0;
            private boolean isSwiping = false;

            @Override
            public boolean onTouch(View v, android.view.MotionEvent event) {
                switch (event.getActionMasked()) {
                    case android.view.MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        startY = event.getRawY();
                        isSwiping = false;
                        break;

                    case android.view.MotionEvent.ACTION_MOVE:
                        float diffX = event.getRawX() - startX;
                        float diffY = event.getRawY() - startY;
                        if (!isSwiping && Math.abs(diffX) > 40 && Math.abs(diffX) > Math.abs(diffY)) {
                            isSwiping = true;
                            if (v.getParent() != null) v.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        if (isSwiping) return true;
                        break;

                    case android.view.MotionEvent.ACTION_UP:
                        if (isSwiping) {
                            float finalDiffX = event.getRawX() - startX;
                            if (finalDiffX > 120) {
                                // Slide vers la DROITE : Retour à l'écran précédent
                                navController.popBackStack();
                            } else if (finalDiffX < -120) {
                                // Slide vers la GAUCHE : Vin suivant
                                if (viewModel.nextWine()) {
                                    HapticUtil.playConfirm(requireView());
                                }
                            }
                            isSwiping = false;
                            return true;
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tvTitle);
        tvCepage = view.findViewById(R.id.tvCepage);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvType = view.findViewById(R.id.tvType);
        fabFavorite = view.findViewById(R.id.fabFavorite);

        // Meal pairing
        layoutMealPairing = view.findViewById(R.id.layoutMealPairing);
        tvMealPairingName = view.findViewById(R.id.tvMealPairingName);
        tvMealPairingIngredients = view.findViewById(R.id.tvMealPairingIngredients);
    }

    private void setupListeners() {
        if (fabFavorite != null) {
            fabFavorite.setOnClickListener(v -> {
                viewModel.toggleFavorite();
                HapticUtil.playConfirm(v);
                animateFavoriteButton();
            });
        }

        // Clic sur la section plat
        if (layoutMealPairing != null) {
            layoutMealPairing.setOnClickListener(v -> {
                HapticUtil.playLightClick(v);
                openMealDetails();
            });
        }
    }

    private void openMealDetails() {
        if (currentPairedMeal == null) return;

        MealDetailsBottomSheetFragment bottomSheet = MealDetailsBottomSheetFragment.newInstance(currentPairedMeal);
        bottomSheet.show(getParentFragmentManager(), "MealDetails");
    }

    private void observeViewModel() {
        viewModel.getCurrentWine().observe(getViewLifecycleOwner(), wine -> {
            if (wine != null) {
                displayWine(wine);
                loadPairedMeal(wine);
            }
        });

        viewModel.getIsFavorite().observe(getViewLifecycleOwner(), isFavorite -> {
            updateFavoriteIcon(isFavorite != null && isFavorite);
        });
    }

    private void handleArguments() {
        if (getArguments() != null) {
            WineDto wine = (WineDto) getArguments().getSerializable("wine");
            if (wine != null) {
                viewModel.setCurrentWine(wine);
            }
        }
    }

    /**
     * Charge un plat accordé avec le vin
     */
    private void loadPairedMeal(WineDto wine) {
        if (layoutMealPairing == null) return;

        // Afficher le loading
        if (tvMealPairingName != null) tvMealPairingName.setText(getString(R.string.loading));
        if (tvMealPairingIngredients != null) tvMealPairingIngredients.setText("");
        layoutMealPairing.setVisibility(View.VISIBLE);

        // Trouver la catégorie compatible
        String category = WineFoodPairingUtil.getWeeklyCategory(wine);

        // Charger les plats de cette catégorie
        Repository.getInstance().getMealsByCategory(category).observe(getViewLifecycleOwner(), state -> {
            if (state.isSuccess() && state.getData() != null &&
                state.getData().getMeals() != null && !state.getData().getMeals().isEmpty()) {

                List<MealDto> meals = state.getData().getMeals();

                // Choisir un plat aléatoire basé sur l'ID du vin
                Random random = new Random(wine.getId());
                int index = random.nextInt(meals.size());
                MealDto selectedMeal = meals.get(index);

                // Charger les détails du plat
                Repository.getInstance().getMealDetails(selectedMeal.getIdMeal()).observe(getViewLifecycleOwner(), detailState -> {
                    if (detailState.isSuccess() && detailState.getData() != null &&
                        detailState.getData().getMeals() != null && !detailState.getData().getMeals().isEmpty()) {

                        MealDto fullMeal = detailState.getData().getMeals().get(0);

                        // Traduire le plat
                        MealTranslationManager.getInstance().translateMeal(fullMeal, translatedMeal -> {
                            if (!isAdded()) return;
                            currentPairedMeal = translatedMeal;
                            displayPairedMeal(translatedMeal);
                        });

                    } else {
                        // Utiliser le plat sans détails
                        currentPairedMeal = selectedMeal;
                        displayPairedMeal(selectedMeal);
                    }
                });

            } else if (state.isError()) {
                layoutMealPairing.setVisibility(View.GONE);
            }
        });
    }

    private void displayPairedMeal(MealDto meal) {
        if (meal == null || layoutMealPairing == null) return;

        layoutMealPairing.setVisibility(View.VISIBLE);

        if (tvMealPairingName != null) {
            tvMealPairingName.setText(meal.getName() != null ? meal.getName() : meal.getStrMeal());
        }

        if (tvMealPairingIngredients != null) {
            List<String> ingredients = meal.getIngredients();
            List<String> measures = meal.getMeasures();

            if (ingredients != null && !ingredients.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                int max = Math.min(4, ingredients.size());

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

                if (ingredients.size() > 4) {
                    sb.append("\n+ ").append(ingredients.size() - 4).append(" autres...");
                }

                tvMealPairingIngredients.setText(sb.toString());
            } else {
                tvMealPairingIngredients.setText(getString(R.string.weekly_no_ingredients));
            }
        }
    }

    private void displayWine(WineDto wine) {
        if (wine == null) return;

        if (tvTitle != null) {
            tvTitle.setText(wine.getTitle() != null ? wine.getTitle() : getString(R.string.result_unknown_wine));
        }

        if (tvCepage != null) {
            tvCepage.setText(wine.getVariety() != null && !wine.getVariety().isEmpty() ? wine.getVariety() : "-");
        }

        if (tvDescription != null) {
            tvDescription.setText(wine.getDescription() != null && !wine.getDescription().isEmpty() ? wine.getDescription() : getString(R.string.result_no_description));
        }

        if (tvType != null) {
            tvType.setText(wine.getColorDisplayName());
        }

    }

    private void updateFavoriteIcon(boolean isFavorite) {
        if (fabFavorite != null && isAdded()) {
            fabFavorite.setImageResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
            if (isFavorite) {
                fabFavorite.setImageTintList(null);
            } else {
                fabFavorite.setImageTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), R.color.secondary));
            }
        }
    }



    private void animateFavoriteButton() {
        if (fabFavorite != null) {
            fabFavorite.animate()
                    .scaleX(1.3f).scaleY(1.3f)
                    .setDuration(100)
                    .withEndAction(() -> fabFavorite.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                    .start();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tvTitle = null;
        tvCepage = null;
        tvDescription = null;
        tvType = null;
        fabFavorite = null;
        layoutMealPairing = null;
        tvMealPairingName = null;
        tvMealPairingIngredients = null;
    }
}