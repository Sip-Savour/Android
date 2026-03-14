package com.sipandsavour;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

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

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView bottomNav;
    private View navHostFragmentView;

    // Destinations où la bottom nav doit être CACHÉE
    private static final Set<Integer> HIDE_BOTTOM_NAV = new HashSet<>(Arrays.asList(
            R.id.splashFragment,
            R.id.loginFragment,
            R.id.registerFragment
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_SipSavour);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNavigation();
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navHostFragment);

        if (navHostFragment == null) {
            throw new IllegalStateException("NavHostFragment not found");
        }

        navController = navHostFragment.getNavController();
        bottomNav = findViewById(R.id.bottomNav);
        navHostFragmentView = findViewById(R.id.navHostFragment);

        NavigationUI.setupWithNavController(bottomNav, navController);

        navController.addOnDestinationChangedListener(this::onDestinationChanged);
    }

    private void onDestinationChanged(
            @NonNull NavController controller,
            @NonNull NavDestination destination,
            Bundle arguments) {

        int destId = destination.getId();

        if (HIDE_BOTTOM_NAV.contains(destId)) {
            hideBottomNav();
        } else {
            showBottomNav();
        }
    }

    private void showBottomNav() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)
                navHostFragmentView.getLayoutParams();
        params.bottomMargin = getResources()
                .getDimensionPixelSize(R.dimen.bottom_nav_height);
        navHostFragmentView.setLayoutParams(params);

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
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)
                navHostFragmentView.getLayoutParams();
        params.bottomMargin = 0;
        navHostFragmentView.setLayoutParams(params);

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