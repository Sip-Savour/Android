package com.sipnsavour.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sipnsavour.adapter.CategoryAdapter;
import com.sipnsavour.model.dto.Category;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ImageView ivBack;
    private ChipGroup chipGroupFlavors;
    private RecyclerView rvCategories;
    private Button btnFilter;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private List<String> selectedFlavors;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        setupBottomNavigation();
        loadCategories();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        chipGroupFlavors = findViewById(R.id.chip_group_flavors);
        rvCategories = findViewById(R.id.rv_categories);
        btnFilter = findViewById(R.id.btn_filter);
        bottomNav = findViewById(R.id.bottom_navigation);
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

        btnFilter.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, SuggestionActivity.class);
            intent.putStringArrayListExtra("selected_flavors", (ArrayList<String>) selectedFlavors);
            startActivity(intent);
        });

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

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_search);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(SearchActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_search) {
                return true;
            } else if (itemId == R.id.nav_wine) {
                startActivity(new Intent(SearchActivity.this, WineListActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_favorites) {
                startActivity(new Intent(SearchActivity.this, FavoritesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(SearchActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void loadCategories() {
        categoryList.add(new Category("Viandes", false));
        categoryList.add(new Category("Poissons", false));
        categoryList.add(new Category("Fromages", false));
        categoryList.add(new Category("Desserts", false));
        categoryList.add(new Category("Légumes", false));
        categoryAdapter.notifyDataSetChanged();
    }
}