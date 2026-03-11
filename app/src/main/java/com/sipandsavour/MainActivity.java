package com.sipandsavour;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Activity principale (Single Activity Architecture).
 * Gère la navigation et la BottomNavigationView.
 */
public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView bottomNav;

    // Destinations où la bottom nav doit être visible
    private static final Set<Integer> BOTTOM_NAV_DESTINATIONS = new HashSet<>(Arrays.asList(
            R.id.homeFragment,
            R.id.advancedSearchFragment,
            R.id.weeklyChoiceFragment,
            R.id.favoritesFragment,
            R.id.profileFragment
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Changer le thème après le splash (avant super.onCreate)
        setTheme(R.style.Theme_SipSavour);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNavigation();
    }

    private void setupNavigation() {
        // Récupérer le NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navHostFragment);

        if (navHostFragment == null) {
            throw new IllegalStateException("NavHostFragment not found");
        }

        navController = navHostFragment.getNavController();
        bottomNav = findViewById(R.id.bottomNav);

        // Lier la BottomNav au NavController
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Observer les changements de destination pour afficher/masquer la bottom nav
        navController.addOnDestinationChangedListener(this::onDestinationChanged);
    }

    /**
     * Gère l'affichage de la BottomNav selon la destination
     */
    private void onDestinationChanged(
            @NonNull NavController controller,
            @NonNull NavDestination destination,
            Bundle arguments) {

        int destId = destination.getId();

        if (BOTTOM_NAV_DESTINATIONS.contains(destId)) {
            showBottomNav();
        } else {
            hideBottomNav();
        }
    }

    private void showBottomNav() {
        if (bottomNav.getVisibility() != View.VISIBLE) {
            bottomNav.setVisibility(View.VISIBLE);
            bottomNav.animate()
                    .translationY(0)
                    .alpha(1f)
                    .setDuration(200)
                    .start();
        }
    }

    private void hideBottomNav() {
        if (bottomNav.getVisibility() == View.VISIBLE) {
            bottomNav.animate()
                    .translationY(bottomNav.getHeight())
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction(() -> bottomNav.setVisibility(View.GONE))
                    .start();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}