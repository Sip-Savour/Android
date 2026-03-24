package com.sipandsavour.ui.home;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.sipandsavour.R;
import com.sipandsavour.data.SessionManager;

/**
 * Fragment de splash screen avec animation du logo.
 */
public class SplashFragment extends Fragment {

    private static final long SPLASH_DELAY = 2000L;
    private static final long ANIM_DURATION = 800L;

    private ImageView ivLogo;
    private TextView tvAppName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        startSplashAnimation();

        new Handler(Looper.getMainLooper()).postDelayed(this::navigateToNextScreen, SPLASH_DELAY);
    }

    private void bindViews(View view) {
        ivLogo = view.findViewById(R.id.ivSplashLogo);
        tvAppName = view.findViewById(R.id.tvSplashName);
    }

    private void startSplashAnimation() {
        // État initial
        ivLogo.setScaleX(0f);
        ivLogo.setScaleY(0f);
        ivLogo.setAlpha(0f);
        tvAppName.setAlpha(0f);
        tvAppName.setTranslationY(50f);

        // Animation du logo
        ObjectAnimator logoScaleX = ObjectAnimator.ofFloat(ivLogo, "scaleX", 0f, 1f);
        ObjectAnimator logoScaleY = ObjectAnimator.ofFloat(ivLogo, "scaleY", 0f, 1f);
        ObjectAnimator logoAlpha = ObjectAnimator.ofFloat(ivLogo, "alpha", 0f, 1f);

        AnimatorSet logoAnimator = new AnimatorSet();
        logoAnimator.playTogether(logoScaleX, logoScaleY, logoAlpha);
        logoAnimator.setDuration(ANIM_DURATION);
        logoAnimator.setInterpolator(new OvershootInterpolator(1.2f));

        // Animation du texte
        ObjectAnimator textAlpha = ObjectAnimator.ofFloat(tvAppName, "alpha", 0f, 1f);
        ObjectAnimator textTranslate = ObjectAnimator.ofFloat(tvAppName, "translationY", 50f, 0f);

        AnimatorSet textAnimator = new AnimatorSet();
        textAnimator.playTogether(textAlpha, textTranslate);
        textAnimator.setDuration(ANIM_DURATION);
        textAnimator.setStartDelay(400L);

        logoAnimator.start();
        textAnimator.start();
    }

    private void navigateToNextScreen() {
        if (!isAdded()) return;

        NavController navController = NavHostFragment.findNavController(this);

        // Vérification de l'état de connexion via le SessionManager
        boolean isLoggedIn = SessionManager.getInstance().isLoggedIn();

        if (isLoggedIn) {
            navController.navigate(R.id.action_splash_to_home);
        } else {
            navController.navigate(R.id.action_splash_to_auth);
        }
    }
}