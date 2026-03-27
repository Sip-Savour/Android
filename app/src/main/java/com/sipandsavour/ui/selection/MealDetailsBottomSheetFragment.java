package com.sipandsavour.ui.selection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sipandsavour.R;
import com.sipandsavour.data.dto.meal.MealDto;
import com.sipandsavour.util.MealTranslationManager;

/**
 * Bottom Sheet pour afficher les détails complètes d'une recette
 */
public class MealDetailsBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_MEAL_ID = "meal_id";
    private static final String ARG_MEAL = "meal";

    private TextView tvMealDetailTitle;
    private TextView tvMealDetailInstructions;
    private LinearLayout ingredientsContainer;
    private ImageButton btnCloseModal;

    private MealDto meal;

    public static MealDetailsBottomSheetFragment newInstance(MealDto meal) {
        MealDetailsBottomSheetFragment fragment = new MealDetailsBottomSheetFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MEAL, meal);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_meal_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvMealDetailTitle = view.findViewById(R.id.tvMealDetailTitle);
        tvMealDetailInstructions = view.findViewById(R.id.tvMealDetailInstructions);
        ingredientsContainer = view.findViewById(R.id.ingredientsContainer);
        btnCloseModal = view.findViewById(R.id.btnCloseModal);

        btnCloseModal.setOnClickListener(v -> dismiss());

        // Récupérer la recette depuis les arguments
        if (getArguments() != null) {
            meal = (MealDto) getArguments().getSerializable(ARG_MEAL);
        }

        if (meal != null) {
            displayMealDetails();
        }
    }

    private void displayMealDetails() {
        // Traduire la recette si nécessaire
        MealTranslationManager.getInstance().translateMealIfNeeded(meal, translatedMeal -> {
            if (translatedMeal != null) {
                tvMealDetailTitle.setText(translatedMeal.getStrMeal());
                tvMealDetailInstructions.setText(translatedMeal.getStrInstructions());
                displayIngredients(translatedMeal);
            }
        });
    }

    private void displayIngredients(MealDto meal) {
        ingredientsContainer.removeAllViews();

        // Les ingrédients sont dans strIngredient1-20 et strMeasure1-20
        String[] ingredients = {
                meal.getStrIngredient1(), meal.getStrIngredient2(), meal.getStrIngredient3(),
                meal.getStrIngredient4(), meal.getStrIngredient5(), meal.getStrIngredient6(),
                meal.getStrIngredient7(), meal.getStrIngredient8(), meal.getStrIngredient9(),
                meal.getStrIngredient10(), meal.getStrIngredient11(), meal.getStrIngredient12(),
                meal.getStrIngredient13(), meal.getStrIngredient14(), meal.getStrIngredient15(),
                meal.getStrIngredient16(), meal.getStrIngredient17(), meal.getStrIngredient18(),
                meal.getStrIngredient19(), meal.getStrIngredient20()
        };

        String[] measures = {
                meal.getStrMeasure1(), meal.getStrMeasure2(), meal.getStrMeasure3(),
                meal.getStrMeasure4(), meal.getStrMeasure5(), meal.getStrMeasure6(),
                meal.getStrMeasure7(), meal.getStrMeasure8(), meal.getStrMeasure9(),
                meal.getStrMeasure10(), meal.getStrMeasure11(), meal.getStrMeasure12(),
                meal.getStrMeasure13(), meal.getStrMeasure14(), meal.getStrMeasure15(),
                meal.getStrMeasure16(), meal.getStrMeasure17(), meal.getStrMeasure18(),
                meal.getStrMeasure19(), meal.getStrMeasure20()
        };

        // On affiche que si l'ingrédient n'est pas vide
        for (int i = 0; i < ingredients.length; i++) {
            String ingredient = ingredients[i];
            String measure = measures[i];

            if (ingredient != null && !ingredient.isEmpty() && !ingredient.equals("null")) {
                addIngredientRow(ingredient, measure);
            }
        }
    }

    private void addIngredientRow(String ingredient, String measure) {
        TextView ingredientView = new TextView(requireContext());
        ingredientView.setText("• " + ingredient + (measure != null && !measure.isEmpty() && !measure.equals("null") ? " - " + measure : ""));
        ingredientView.setTextColor(requireContext().getColor(android.R.color.white));
        ingredientView.setTextSize(14);
        ingredientView.setPadding(0, 8, 0, 8);

        ingredientsContainer.addView(ingredientView);
    }
}


