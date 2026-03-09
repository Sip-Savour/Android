package com.sipnsavour.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private CardView cvWeeklySelection;
    private CardView cvChooseFlavors;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupClickListeners();
        setupBottomNavigation();
    }

    private void initializeViews() {
        cvWeeklySelection = findViewById(R.id.cv_weekly_selection);
        cvChooseFlavors = findViewById(R.id.cv_choose_flavors);
        bottomNav = findViewById(R.id.bottom_navigation);
    }

    private void setupClickListeners() {
        cvWeeklySelection.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WeeklyChoiceActivity.class);
            startActivity(intent);
        });

        cvChooseFlavors.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChooseFlavorsActivity.class);
            startActivity(intent);
        });
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_search) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_wine) {
                startActivity(new Intent(MainActivity.this, WineListActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_favorites) {
                startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}