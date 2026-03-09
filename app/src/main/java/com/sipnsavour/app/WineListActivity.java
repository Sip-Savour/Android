package com.sipnsavour.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sipnsavour.model.dto.Wine;
import com.sipnsavour.app.adapter.WineGridAdapter;

import java.util.ArrayList;
import java.util.List;

public class WineListActivity extends AppCompatActivity {

    private ImageView ivBack, ivToggleView;
    private SearchView searchView;
    private ChipGroup chipGroupFilter;
    private RecyclerView rvWineList;
    private TextView tvResultCount;
    private LinearLayout emptyView;
    private WineGridAdapter wineGridAdapter;
    private List<Wine> wineList;
    private List<Wine> filteredWineList;
    private BottomNavigationView bottomNav;
    private String currentFilter = "Tous";
    private boolean isGridView = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wine_list_grid);

        initializeViews();
        setupRecyclerView();
        setupSearchView();
        setupFilterChips();
        setupClickListeners();
        setupBottomNavigation();
        loadWines();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        ivToggleView = findViewById(R.id.iv_toggle_view);
        searchView = findViewById(R.id.search_view);
        chipGroupFilter = findViewById(R.id.chip_group_filter);
        rvWineList = findViewById(R.id.rv_wine_list);
        tvResultCount = findViewById(R.id.tv_result_count);
        emptyView = findViewById(R.id.empty_view);
        bottomNav = findViewById(R.id.bottom_navigation);
    }

    private void setupRecyclerView() {
        wineList = new ArrayList<>();
        filteredWineList = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvWineList.setLayoutManager(gridLayoutManager);

        wineGridAdapter = new WineGridAdapter(this, filteredWineList, new WineGridAdapter.OnWineClickListener() {
            @Override
            public void onWineClick(Wine wine) {
                Intent intent = new Intent(WineListActivity.this, WineInfoActivity.class);
                // Passer les données du vin
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(Wine wine, int position) {
                wine.setFavorite(!wine.isFavorite());
                wineGridAdapter.notifyItemChanged(position);
            }
        });

        rvWineList.setAdapter(wineGridAdapter);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterWines(query, currentFilter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterWines(newText, currentFilter);
                return true;
            }
        });
    }

    private void setupFilterChips() {
        chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip chip = findViewById(checkedIds.get(0));
                if (chip != null) {
                    currentFilter = chip.getText().toString();
                    filterWines(searchView.getQuery().toString(), currentFilter);
                }
            }
        });
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        ivToggleView.setOnClickListener(v -> toggleViewMode());
    }

    private void toggleViewMode() {
        isGridView = !isGridView;

        if (isGridView) {
            ivToggleView.setImageResource(R.drawable.ic_list_view);
            rvWineList.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            ivToggleView.setImageResource(R.drawable.ic_grid_view);
            rvWineList.setLayoutManager(new GridLayoutManager(this, 1));
        }

        wineGridAdapter.setGridView(isGridView);
        wineGridAdapter.notifyDataSetChanged();
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_wine);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(WineListActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_search) {
                startActivity(new Intent(WineListActivity.this, SearchActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_wine) {
                return true;
            } else if (itemId == R.id.nav_favorites) {
                startActivity(new Intent(WineListActivity.this, FavoritesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(WineListActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void loadWines() {
        // Simulation de données - À remplacer par un appel API
        wineList.add(new Wine("Château Margaux", "Un vin élégant avec des notes de cassis et de cèdre", "Rouge"));
        wineList.add(new Wine("Chablis Premier Cru", "Vin blanc frais aux arômes d'agrumes", "Blanc"));
        wineList.add(new Wine("Côtes de Provence", "Rosé délicat avec des notes de fruits rouges", "Rosé"));
        wineList.add(new Wine("Barolo Riserva", "Vin rouge puissant aux tanins structurés", "Rouge"));
        wineList.add(new Wine("Sancerre", "Vin blanc vif et minéral", "Blanc"));
        wineList.add(new Wine("Champagne Brut", "Effervescent élégant aux bulles fines", "Blanc"));
        wineList.add(new Wine("Shiraz", "Vin rouge intense aux notes épicées", "Rouge"));
        wineList.add(new Wine("Riesling Alsace", "Blanc aromatique légèrement sucré", "Blanc"));
        wineList.add(new Wine("Bandol Rosé", "Rosé de Provence structuré", "Rosé"));
        wineList.add(new Wine("Châteauneuf", "Rouge complexe et généreux", "Rouge"));
        wineList.add(new Wine("Pouilly-Fuissé", "Chardonnay élégant de Bourgogne", "Blanc"));
        wineList.add(new Wine("Tavel", "Le roi des rosés", "Rosé"));

        filteredWineList.addAll(wineList);
        updateResultCount();
        wineGridAdapter.notifyDataSetChanged();
    }

    private void filterWines(String query, String type) {
        filteredWineList.clear();

        for (Wine wine : wineList) {
            boolean matchesQuery = query.isEmpty() ||
                wine.getCepage().toLowerCase().contains(query.toLowerCase()) ||
                wine.getDescription().toLowerCase().contains(query.toLowerCase());

            boolean matchesType = type.equals("Tous") || wine.getType().equals(type);

            if (matchesQuery && matchesType) {
                filteredWineList.add(wine);
            }
        }

        updateResultCount();
        wineGridAdapter.notifyDataSetChanged();

        // Afficher/masquer le message vide
        if (filteredWineList.isEmpty()) {
            rvWineList.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            rvWineList.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void updateResultCount() {
        String countText = filteredWineList.size() + " vins trouvés";
        tvResultCount.setText(countText);
    }
}