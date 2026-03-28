package com.sipandsavour.ui.home;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.sipandsavour.R;
import com.sipandsavour.data.SessionManager;
import com.sipandsavour.util.Constants;
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

    // Vues animées de la carte recherche
    private View iconHalo;
    private View imgSearchIcon;

    private Animation pulseIconAnim;
    private Animation pulseHaloAnim;

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

        // Vérifier si l'utilisateur est connecté via SessionManager
        boolean isLoggedIn = SessionManager.getInstance().isLoggedIn();

        if (!isLoggedIn) {
            navController.navigate(R.id.nav_auth);
            return;
        }

        bindViews(view);
        loadAnimations();
        setupListeners();
        setupShakeDetection();

        // 🆕 Afficher le popup de shake si jamais montré
        showShakeHintIfNeeded();
    }

    /**
     * 🆕 Affiche le popup d'onboarding pour le shake
     */
    private void showShakeHintIfNeeded() {
        // Vérifier si déjà montré
        boolean alreadyShown = requireContext()
                .getSharedPreferences(Constants.PREF_SESSION, Context.MODE_PRIVATE)
                .getBoolean(Constants.KEY_SHAKE_HINT_SHOWN, false);

        if (alreadyShown) {
            return;
        }

        // Créer le dialog
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_shake_hint);
        dialog.setCancelable(true);

        // Rendre le fond transparent
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        // Animation de l'icône shake
        View ivShakeIcon = dialog.findViewById(R.id.ivShakeIcon);
        if (ivShakeIcon != null) {
            Animation shakeAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.shake_icon);
            ivShakeIcon.startAnimation(shakeAnim);
        }

        // Bouton "Compris !"
        MaterialButton btnGotIt = dialog.findViewById(R.id.btnGotIt);
        btnGotIt.setOnClickListener(v -> {
            HapticUtil.playConfirm(v);
            markShakeHintAsShown();
            dialog.dismiss();
        });

        // Bouton "Ne plus afficher"
        MaterialButton btnDontShowAgain = dialog.findViewById(R.id.btnDontShowAgain);
        btnDontShowAgain.setOnClickListener(v -> {
            HapticUtil.playLightClick(v);
            markShakeHintAsShown();
            dialog.dismiss();
        });

        // Afficher le dialog avec un léger délai pour une meilleure UX
        requireView().postDelayed(() -> {
            if (isAdded() && !dialog.isShowing()) {
                dialog.show();
            }
        }, 500);
    }

    /**
     * 🆕 Marque le popup comme déjà affiché
     */
    private void markShakeHintAsShown() {
        requireContext()
                .getSharedPreferences(Constants.PREF_SESSION, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(Constants.KEY_SHAKE_HINT_SHOWN, true)
                .apply();
    }

    private void setupShakeDetection() {
        mSensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mShakeDetector = new ShakeDetector();

            mShakeDetector.setOnShakeListener(count -> {
                if (isAdded()
                        && navController != null
                        && navController.getCurrentDestination() != null
                        && navController.getCurrentDestination().getId() == R.id.homeFragment) {

                    mSensorManager.unregisterListener(mShakeDetector);

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
        // Lancer les animations de la carte recherche
        startSearchCardAnimations();
    }

    @Override
    public void onPause() {
        // Désactiver le détecteur quand on change d'écran
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mShakeDetector);
        }
        // Arrêter les animations pour économiser la batterie
        stopSearchCardAnimations();
        super.onPause();
    }

    private void bindViews(View view) {
        swipeRefresh    = view.findViewById(R.id.swipeRefresh);
        cardWeekly      = view.findViewById(R.id.cardWeekly);
        cardSearch      = view.findViewById(R.id.cardSearch);
        shimmerContainer = view.findViewById(R.id.shimmerContainer);

        // Vues animées
        iconHalo       = view.findViewById(R.id.iconHalo);
        imgSearchIcon  = view.findViewById(R.id.imgSearchIcon);
    }

    private void loadAnimations() {
        pulseIconAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.pulse_icon);
        pulseHaloAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.pulse_halo);
    }

    private void startSearchCardAnimations() {
        if (imgSearchIcon != null && pulseIconAnim != null) {
            imgSearchIcon.startAnimation(pulseIconAnim);
        }
        if (iconHalo != null && pulseHaloAnim != null) {
            iconHalo.startAnimation(pulseHaloAnim);
        }
    }

    private void stopSearchCardAnimations() {
        if (imgSearchIcon != null) {
            imgSearchIcon.clearAnimation();
        }
        if (iconHalo != null) {
            iconHalo.clearAnimation();
        }
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