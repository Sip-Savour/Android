package com.sipandsavour.ui.result;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

// Nouveaux imports nécessaires pour le swipe fiable
import android.view.GestureDetector;
import android.view.MotionEvent;
import androidx.core.view.GestureDetectorCompat;

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

    private MealDto currentPairedMeal;
    private GestureDetectorCompat gestureDetector;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        View scrollView = view.findViewById(R.id.scrollView);

        // CORRECTION MAJEURE : On utilise l'outil officiel d'Android pour détecter les gestes secs (Fling)
        gestureDetector = new GestureDetectorCompat(requireContext(), new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;

                float diffY = e2.getRawY() - e1.getRawY();
                float diffX = e2.getRawX() - e1.getRawX();

                // On vérifie que c'est bien un mouvement horizontal
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            // Swipe vers la DROITE : Retour en arrière
                            navController.popBackStack();
                        } else {
                            // Swipe vers la GAUCHE : Vin suivant
                            if (viewModel.nextWine()) {
                                HapticUtil.playConfirm(requireView());
                            } else {
                                // Petit retour visuel si on arrive à la fin de la liste !
                                Toast.makeText(requireContext(), "Dernier vin atteint", Toast.LENGTH_SHORT).show();
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        // On n'attache l'écouteur qu'au ScrollView pour éviter les conflits
        if (scrollView != null) {
            scrollView.setOnTouchListener((v, event) -> {
                gestureDetector.onTouchEvent(event);
                return false; // Très important : False permet au ScrollView de continuer à défiler de haut en bas !
            });
        }
    }

    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tvTitle);
        tvCepage = view.findViewById(R.id.tvCepage);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvType = view.findViewById(R.id.tvType);
        fabFavorite = view.findViewById(R.id.fabFavorite);

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

    private void loadPairedMeal(WineDto wine) {
        if (layoutMealPairing == null) return;

        if (tvMealPairingName != null) tvMealPairingName.setText(getString(R.string.loading));
        if (tvMealPairingIngredients != null) tvMealPairingIngredients.setText("");
        layoutMealPairing.setVisibility(View.VISIBLE);

        String category = WineFoodPairingUtil.getWeeklyCategory(wine);

        Repository.getInstance().getMealsByCategory(category).observe(getViewLifecycleOwner(), state -> {
            if (state.isSuccess() && state.getData() != null &&
                    state.getData().getMeals() != null && !state.getData().getMeals().isEmpty()) {

                List<MealDto> meals = state.getData().getMeals();
                Random random = new Random(wine.getId());
                int index = random.nextInt(meals.size());
                MealDto selectedMeal = meals.get(index);

                Repository.getInstance().getMealDetails(selectedMeal.getIdMeal()).observe(getViewLifecycleOwner(), detailState -> {
                    if (detailState.isSuccess() && detailState.getData() != null &&
                            detailState.getData().getMeals() != null && !detailState.getData().getMeals().isEmpty()) {

                        MealDto fullMeal = detailState.getData().getMeals().get(0);
                        MealTranslationManager.getInstance().translateMeal(fullMeal, translatedMeal -> {
                            if (!isAdded()) return;
                            currentPairedMeal = translatedMeal;
                            displayPairedMeal(translatedMeal);
                        });

                    } else {
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
                // CORRECTION : On force un rose vif absolu garanti (Hexadécimal #E91E63)
                fabFavorite.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#E91E63")));
            } else {
                // Reste gris/pêche
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