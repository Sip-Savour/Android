package com.sipnsavour.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sipnsavour.adapter.FavoriteAdapter;
import com.sipnsavour.model.dto.Wine;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private ImageView ivBack;
    private RecyclerView rvFavorites;
    private FavoriteAdapter favoriteAdapter;
    private List<Wine> favoriteWines;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        setupBottomNavigation();
        loadFavorites();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        rvFavorites = findViewById(R.id.rv_favorites);
        bottomNav = findViewById(R.id.bottom_navigation);
    }

    private void setupRecyclerView() {
        favoriteWines = new ArrayList<>();
        favoriteAdapter = new FavoriteAdapter(this, favoriteWines, wine -> {
            Intent intent = new Intent(FavoritesActivity.this, WineInfoActivity.class);
            startActivity(intent);
        });
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        rvFavorites.setAdapter(favoriteAdapter);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_favorites);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(FavoritesActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_search) {
                startActivity(new Intent(FavoritesActivity.this, SearchActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_wine) {
                startActivity(new Intent(FavoritesActivity.this, WineListActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_favorites) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(FavoritesActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void loadFavorites() {
        favoriteWines.add(new Wine("Shiraz", "Description du vin...", "Rouge"));
        favoriteWines.add(new Wine("Chardonnay", "Description du vin...", "Blanc"));
        favoriteWines.add(new Wine("Merlot", "Description du vin...", "Rouge"));
        favoriteWines.add(new Wine("Pinot Noir", "Description du vin...", "Rouge"));
        favoriteAdapter.notifyDataSetChanged();
    }
}