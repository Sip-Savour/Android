package com.sipandsavour.ui.selection;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sipandsavour.R;
import com.sipandsavour.data.dto.meal.MealDto;
import com.sipandsavour.util.MealTranslationManager;

import java.util.List;

public class MealDetailsBottomSheetFragment extends BottomSheetDialogFragment {
    private static final String TAG = "MealDetails";
    private static final String ARG_MEAL = "meal";
    private MealDto meal;

    private TextView tvMealDetailTitle;
    private TextView tvMealDetailCategory;
    private TextView tvMealDetailInstructions;
    private LinearLayout ingredientsContainer;
    private ImageButton btnCloseModal;

    public static MealDetailsBottomSheetFragment newInstance(MealDto meal) {
        MealDetailsBottomSheetFragment fragment = new MealDetailsBottomSheetFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MEAL, meal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            meal = (MealDto) getArguments().getSerializable(ARG_MEAL);
        }
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
        initViews(view);
        displayMealDetails();
    }

    private void initViews(View view) {
        tvMealDetailTitle = view.findViewById(R.id.tvMealDetailTitle);
        tvMealDetailCategory = view.findViewById(R.id.tvMealDetailCategory);
        tvMealDetailInstructions = view.findViewById(R.id.tvMealDetailInstructions);
        ingredientsContainer = view.findViewById(R.id.ingredientsContainer);
        btnCloseModal = view.findViewById(R.id.btnCloseModal);

        if (btnCloseModal != null) {
            btnCloseModal.setOnClickListener(v -> dismiss());
        }
    }

    private void displayMealDetails() {
        if (meal == null) {
            Log.e(TAG, "Meal is null!");
            dismiss();
            return;
        }

        Log.d(TAG, "Displaying meal: " + meal.getStrMeal());

        // Afficher les données originales immédiatement
        showMealData(meal);

        // Puis traduire et mettre à jour
        MealTranslationManager.getInstance().translateMealIfNeeded(meal, translatedMeal -> {
            if (translatedMeal == null || !isAdded() || getContext() == null) {
                return;
            }

            Log.d(TAG, "Translation received: " + translatedMeal.getName());
            showMealData(translatedMeal);
        });
    }

    private void showMealData(MealDto mealToDisplay) {
        // Titre
        if (tvMealDetailTitle != null) {
            tvMealDetailTitle.setText(mealToDisplay.getName());
        }

        // Catégorie et Zone
        if (tvMealDetailCategory != null) {
            String category = mealToDisplay.getCategory();
            String area = mealToDisplay.getArea();
            StringBuilder subtitle = new StringBuilder();

            if (category != null && !category.isEmpty()) {
                subtitle.append(category);
            }
            if (area != null && !area.isEmpty()) {
                if (subtitle.length() > 0) subtitle.append(" • ");
                subtitle.append(area);
            }

            tvMealDetailCategory.setText(subtitle.toString());
            tvMealDetailCategory.setVisibility(subtitle.length() > 0 ? View.VISIBLE : View.GONE);
        }

        // Instructions formatées
        if (tvMealDetailInstructions != null) {
            String instructions = mealToDisplay.getInstructions();
            tvMealDetailInstructions.setText(formatInstructions(instructions));
        }

        // Ingrédients formatés
        displayFormattedIngredients(mealToDisplay);
    }

    /**
     * Formate les instructions en étapes numérotées
     */
    private String formatInstructions(String instructions) {
        if (instructions == null || instructions.trim().isEmpty()) {
            return "Aucune instruction disponible.";
        }

        // Nettoyer le texte
        instructions = instructions.trim();

        // Vérifier si déjà formaté avec des numéros
        if (instructions.matches("^1\\..*")) {
            return instructions;
        }

        // Diviser par phrases ou retours à la ligne
        String[] sentences = instructions.split("(?<=[.!?])\\s+|\\r\\n\\r\\n|\\n\\n");

        StringBuilder formatted = new StringBuilder();
        int step = 1;

        for (String sentence : sentences) {
            sentence = sentence.trim();

            // Ignorer les lignes vides ou trop courtes
            if (sentence.isEmpty() || sentence.length() < 10) continue;

            // Ignorer si c'est déjà un numéro de step
            if (sentence.matches("^\\d+\\.?\\s*$")) continue;

            // Nettoyer les numéros existants au début
            sentence = sentence.replaceFirst("^\\d+\\.?\\s*", "");

            // Capitaliser la première lettre
            if (sentence.length() > 0) {
                sentence = Character.toUpperCase(sentence.charAt(0)) + sentence.substring(1);
            }

            // Ajouter le point si nécessaire
            if (!sentence.endsWith(".") && !sentence.endsWith("!") && !sentence.endsWith("?")) {
                sentence += ".";
            }

            formatted.append(step).append(". ").append(sentence).append("\n\n");
            step++;
        }

        return formatted.toString().trim();
    }

    /**
     * Affiche les ingrédients formatés avec quantités
     */
    private void displayFormattedIngredients(MealDto mealToDisplay) {
        if (ingredientsContainer == null) return;

        List<String> ingredients = mealToDisplay.getIngredients();
        List<String> measures = mealToDisplay.getMeasures();

        // Clear previous ingredients
        ingredientsContainer.removeAllViews();

        if (ingredients == null || ingredients.isEmpty()) {
            TextView noIngredients = new TextView(requireContext());
            noIngredients.setText("Aucun ingrédient disponible.");
            noIngredients.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray));
            ingredientsContainer.addView(noIngredients);
            return;
        }

        for (int i = 0; i < ingredients.size(); i++) {
            String ingredient = ingredients.get(i);
            String measure = (measures != null && i < measures.size()) ? measures.get(i) : "";

            if (ingredient == null || ingredient.trim().isEmpty()) continue;

            // Créer une ligne horizontale pour chaque ingrédient
            LinearLayout row = new LinearLayout(requireContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 12, 0, 12);
            row.setGravity(Gravity.CENTER_VERTICAL);

            // Bullet point
            TextView bullet = new TextView(requireContext());
            bullet.setText("•  ");
            bullet.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
            bullet.setTextSize(16f);
            row.addView(bullet);

            // Quantité (en gras)
            if (measure != null && !measure.trim().isEmpty()) {
                TextView tvMeasure = new TextView(requireContext());
                tvMeasure.setText(measure.trim() + " ");
                tvMeasure.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
                tvMeasure.setTextSize(14f);
                tvMeasure.setTypeface(null, Typeface.BOLD);
                row.addView(tvMeasure);
            }

            // Ingrédient
            TextView tvIngredient = new TextView(requireContext());
            tvIngredient.setText(ingredient.trim());
            tvIngredient.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
            tvIngredient.setTextSize(14f);
            row.addView(tvIngredient);

            ingredientsContainer.addView(row);

            // Ligne de séparation (sauf pour le dernier)
            if (i < ingredients.size() - 1) {
                View divider = new View(requireContext());
                divider.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray));
                divider.setAlpha(0.3f);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1);
                params.setMargins(32, 0, 0, 0);
                divider.setLayoutParams(params);
                ingredientsContainer.addView(divider);
            }
        }

        Log.d(TAG, "Ingredients displayed: " + ingredients.size());
    }
}