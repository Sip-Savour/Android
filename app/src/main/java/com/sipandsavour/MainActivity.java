package com.sipandsavour;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.splashscreen.SplashScreen;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private NavController navController;
    private BottomNavigationView bottomNav;
    private View navHostFragmentView;

    // ExecutorService pour opérations en arrière-plan
    private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Lecteur audio pour le son de lancement
    private MediaPlayer mediaPlayer;

    // Destinations où la bottom nav doit être CACHÉE
    private static final Set<Integer> HIDE_BOTTOM_NAV = new HashSet<>(Arrays.asList(
            R.id.splashFragment,
            R.id.loginFragment,
            R.id.registerFragment
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        long startTime = System.currentTimeMillis();
        Log.d(TAG, "=== onCreate START ===");

        // 1. Initialiser le gestionnaire de session le plus tôt possible
        SessionManager.init(getApplicationContext());
        Log.d(TAG, "SessionManager.init() in " + (System.currentTimeMillis() - startTime) + "ms");

        // 2. Neutraliser le splash système Android 12+
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> false);

        // 3. LECTURE DU THÈME SAUVEGARDÉ (opération légère)
        int currentThemeCode = SessionManager.getInstance().getTheme();

        if (currentThemeCode == 100) {
            setTheme(R.style.Theme_SipSavour_Jinx);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            setTheme(R.style.Theme_SipSavour);
            AppCompatDelegate.setDefaultNightMode(currentThemeCode);
        }

        Log.d(TAG, "Theme set in " + (System.currentTimeMillis() - startTime) + "ms");

        // 4. Appel à la méthode parente APRES avoir configuré le thème
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "setContentView() in " + (System.currentTimeMillis() - startTime) + "ms");

        // === JOUER LE SON DE LANCEMENT ===
        // Uniquement au premier lancement (pas lors d'une rotation d'écran)
        if (savedInstanceState == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.launch);
            if (mediaPlayer != null) {
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> {
                    mp.release();
                    mediaPlayer = null;
                });
            }
        }

        // 5. Opérations lourdes en ARRIÈRE-PLAN
        initializeHeavyOperationsInBackground();

        setupNavigation();

        Log.d(TAG, "=== onCreate END === TOTAL: " + (System.currentTimeMillis() - startTime) + "ms");
    }

    /**
     * Initialise les opérations lourdes dans un thread séparé
     * pour éviter de bloquer le thread UI principal
     */
    private void initializeHeavyOperationsInBackground() {
        backgroundExecutor.execute(() -> {
            long bgStart = System.currentTimeMillis();
            Log.d(TAG, "=== Background init START ===");

            try {
                // Réinjecter le token de sécurité
                SessionManager.getInstance().restoreSession();
                Log.d(TAG, "restoreSession() in " + (System.currentTimeMillis() - bgStart) + "ms");

                // Initialiser la traduction (Firebase ML Kit)
                TranslationManager.getInstance();
                Log.d(TAG, "TranslationManager init in " + (System.currentTimeMillis() - bgStart) + "ms");

                Log.d(TAG, "=== Background init END === TOTAL: " + (System.currentTimeMillis() - bgStart) + "ms");

            } catch (Exception e) {
                Log.e(TAG, "Error during background initialization", e);
            }
        });
    }

    /**
     * Configure la navigation dans l'application.
     */
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

    /**
     * Appelé lorsque la destination de la navigation change.
     * @param controller Le contrôleur de navigation.
     * @param destination La nouvelle destination.
     * @param arguments Les arguments de la destination.
     */
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

    /**
     * Affiche la barre de navigation en bas.
     */
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

    /**
     * Cache la barre de navigation en bas.
     */
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

    /**
     * Appelé lorsque l'activité est détruite. Permet de nettoyer les ressources utilisées.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Nettoyer l'executor
        backgroundExecutor.shutdown();

        // Nettoyer le lecteur audio s'il est encore en cours
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * Gère la navigation "up" (flèche de retour dans la barre d'action).
     * @return true si la navigation a été gérée, sinon false.
     */
    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}