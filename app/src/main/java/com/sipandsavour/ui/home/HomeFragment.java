package com.sipandsavour.ui.home;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.card.MaterialCardView;
import com.sipandsavour.R;
import com.sipandsavour.util.HapticUtil;
import com.sipandsavour.util.ShakeDetector;

/**
 * Fragment d'accueil avec les deux cartes principales.
 */
public class HomeFragment extends Fragment {

    private SwipeRefreshLayout swipeRefresh;
    private MaterialCardView cardWeekly;
    private MaterialCardView cardSearch;
    private View shimmerContainer;

    private NavController navController;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);

        bindViews(view);
        setupListeners();
        setupShakeDetection();
    }

    private void setupShakeDetection() {
        mSensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mShakeDetector = new ShakeDetector();

            mShakeDetector.setOnShakeListener(count -> {
                // On vérifie qu'on est bien toujours sur la page d'accueil avant de naviguer
                if (isAdded() && navController != null && navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.homeFragment) {

                    // CORRECTION 1 : On désinscrit le capteur IMMÉDIATEMENT pour éviter les rebonds !
                    mSensorManager.unregisterListener(mShakeDetector);

                    // CORRECTION 2 : On sécurise l'appel au haptic
                    View view = getView();
                    if (view != null) HapticUtil.playConfirm(view);

                    navController.navigate(R.id.action_home_to_random);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Activer le capteur uniquement quand l'écran est affiché
        if (mSensorManager != null && mAccelerometer != null) {
            mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        // CORRECTION 3 : Désactiver le détecteur quand on change d'écran pour économiser la batterie
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mShakeDetector);
        }
        super.onPause();
    }

    private void bindViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        cardWeekly = view.findViewById(R.id.cardWeekly);
        cardSearch = view.findViewById(R.id.cardSearch);
        shimmerContainer = view.findViewById(R.id.shimmerContainer);
    }

    private void setupListeners() {
        swipeRefresh.setColorSchemeResources(R.color.primary, R.color.secondary);
        swipeRefresh.setOnRefreshListener(this::onRefresh);

        cardWeekly.setOnClickListener(v -> {
            HapticUtil.playConfirm(v);
            navigateToWeeklyChoice();
        });
        cardSearch.setOnClickListener(v -> {
            HapticUtil.playConfirm(v);
            navigateToFlavorSelection();
        });
    }

    private void onRefresh() {
        swipeRefresh.postDelayed(() -> {
            if (isAdded()) {
                swipeRefresh.setRefreshing(false);
            }
        }, 1000);
    }

    private void navigateToWeeklyChoice() {
        navController.navigate(R.id.action_home_to_weekly);
    }

    private void navigateToFlavorSelection() {
        navController.navigate(R.id.action_home_to_flavor);
    }
}