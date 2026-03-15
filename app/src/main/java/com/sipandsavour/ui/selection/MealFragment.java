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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sipandsavour.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealFragment extends Fragment {

    private NavController navController;
    private SelectionViewModel viewModel;

    private RecyclerView rvCategories;
    private TextView tvSubcategoryTitle;
    private ChipGroup chipGroupSubcategories;
    private MaterialButton btnNextToFlavor;

    private MealCategoryAdapter categoryAdapter;
    private String selectedCategory = null;
    private String selectedSubcategory = null;

    private static final Map<String, List<String>> SUBCATEGORIES = new HashMap<>();

    static {
        SUBCATEGORIES.put("meat", Arrays.asList("Bœuf", "Agneau", "Porc", "Volaille", "Gibier"));
        SUBCATEGORIES.put("fish", Arrays.asList("Poisson blanc", "Poisson gras", "Fruits de mer", "Crustacés"));
        SUBCATEGORIES.put("veggie", Arrays.asList("Légumes grillés", "Salade", "Pâtes", "Risotto"));
        SUBCATEGORIES.put("cheese", Arrays.asList("Fromage doux", "Fromage affiné", "Fromage bleu", "Chèvre"));
    }

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

        bindViews(view);
        setupCategories();
        setupButton();
    }

    private void bindViews(View view) {
        rvCategories = view.findViewById(R.id.rvCategories);
        tvSubcategoryTitle = view.findViewById(R.id.tvSubcategoryTitle);
        chipGroupSubcategories = view.findViewById(R.id.chipGroupSubcategories);
        btnNextToFlavor = view.findViewById(R.id.btnNextToFlavor);
    }

    private void setupCategories() {
        List<MealCategoryAdapter.MealCategory> categories = new ArrayList<>();
        categories.add(new MealCategoryAdapter.MealCategory("meat",
                getString(R.string.category_meat), R.drawable.ic_wine_fork, R.color.category_meat));
        categories.add(new MealCategoryAdapter.MealCategory("fish",
                getString(R.string.category_fish), R.drawable.ic_wine_fork, R.color.category_fish));
        categories.add(new MealCategoryAdapter.MealCategory("veggie",
                getString(R.string.category_veggie), R.drawable.ic_wine_fork, R.color.category_veggie));
        categories.add(new MealCategoryAdapter.MealCategory("cheese",
                getString(R.string.category_cheese), R.drawable.ic_wine_fork, R.color.category_cheese));

        categoryAdapter = new MealCategoryAdapter(categories, this::onCategorySelected);
        rvCategories.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvCategories.setAdapter(categoryAdapter);
    }

    private void onCategorySelected(String categoryKey) {
        selectedCategory = categoryKey;
        selectedSubcategory = null;
        btnNextToFlavor.setEnabled(false);

        List<String> subcategories = SUBCATEGORIES.get(categoryKey);
        if (subcategories != null && !subcategories.isEmpty()) {
            tvSubcategoryTitle.setVisibility(View.VISIBLE);
            chipGroupSubcategories.setVisibility(View.VISIBLE);
            chipGroupSubcategories.removeAllViews();

            for (String sub : subcategories) {
                Chip chip = new Chip(requireContext());
                chip.setText(sub);
                chip.setCheckable(true);
                chip.setChipBackgroundColorResource(R.color.chip_background_selector);
                chip.setTextColor(getResources().getColorStateList(R.color.chip_text_selector, null));
                chip.setChipStrokeColorResource(R.color.chip_stroke_selector);
                chip.setChipStrokeWidth(getResources().getDimension(R.dimen.chip_stroke_width));

                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        selectedSubcategory = sub;
                        btnNextToFlavor.setEnabled(true);
                    }
                });

                chipGroupSubcategories.addView(chip);
            }
        }

        categoryAdapter.setSelectedCategory(categoryKey);
    }

    private void setupButton() {
        btnNextToFlavor.setOnClickListener(v -> {
            if (selectedCategory != null) {
                navController.navigate(R.id.action_meal_to_flavor);
            }
        });
    }
}