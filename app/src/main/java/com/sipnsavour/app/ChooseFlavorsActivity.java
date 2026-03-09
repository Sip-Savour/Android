package com.sipnsavour.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sipnsavour.adapter.CategoryAdapter;
import com.sipnsavour.model.dto.Category;

import java.util.ArrayList;
import java.util.List;

public class ChooseFlavorsActivity extends AppCompatActivity {

    private ImageView ivBack;
    private ChipGroup chipGroupFlavors;
    private RecyclerView rvCategories;
    private Button btnMatch;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private List<String> selectedFlavors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_flavors);

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        loadCategories();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        chipGroupFlavors = findViewById(R.id.chip_group_flavors);
        rvCategories = findViewById(R.id.rv_categories);
        btnMatch = findViewById(R.id.btn_match);
        selectedFlavors = new ArrayList<>();
    }

    private void setupRecyclerView() {
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categoryList);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        rvCategories.setAdapter(categoryAdapter);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        btnMatch.setOnClickListener(v -> {
            // Lancer la recherche de vins correspondants
            Intent intent = new Intent(ChooseFlavorsActivity.this, SuggestionActivity.class);
            intent.putStringArrayListExtra("selected_flavors", (ArrayList<String>) selectedFlavors);
            startActivity(intent);
        });

        // Gestion de la sélection des chips
        chipGroupFlavors.setOnCheckedStateChangeListener((group, checkedIds) -> {
            selectedFlavors.clear();
            for (int checkedId : checkedIds) {
                Chip chip = group.findViewById(checkedId);
                if (chip != null) {
                    selectedFlavors.add(chip.getText().toString());
                }
            }
        });
    }

    private void loadCategories() {
        // Charger les catégories
        categoryList.add(new Category("Viandes", false));
        categoryList.add(new Category("Poissons", false));
        categoryList.add(new Category("Fromages", false));
        categoryList.add(new Category("Desserts", false));
        categoryList.add(new Category("Légumes", false));
        categoryAdapter.notifyDataSetChanged();
    }
}
