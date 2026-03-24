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
import com.sipandsavour.data.SessionManager;
import com.sipandsavour.util.TranslationManager;

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
        // 1. Initialiser le gestionnaire de session le plus tôt possible
        SessionManager.init(getApplicationContext());

        // 2. LECTURE DU THÈME SAUVEGARDÉ POUR L'EASTER EGG JINX
        int currentThemeCode = SessionManager.getInstance().getTheme();

        if (currentThemeCode == 100) {
            // L'utilisateur a activé le JinxTheme via le menu secret !
            setTheme(R.style.Theme_SipSavour_Jinx);
            // On force les popups et éléments natifs Android à rester en mode sombre
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            // Thème standard
            setTheme(R.style.Theme_SipSavour);
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(currentThemeCode);
        }

        // 3. Appel à la méthode parente APRES avoir configuré le thème
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 4. Réinjecter le token de sécurité s'il y en a un
        SessionManager.getInstance().restoreSession();

        // 5. Initialiser la traduction
        TranslationManager.getInstance();

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