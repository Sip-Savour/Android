package com.sipandsavour.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sipandsavour.R;
import com.sipandsavour.data.SessionManager;
import com.sipandsavour.util.HapticUtil;

public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private NavController navController;

    // Views
    private TextView tvProfileName;
    private TextView tvProfileEmail;
    private TextView tvProfileDob;
    private ImageView ivProfileAvatar;
    private MaterialButton btnPreferences;
    private MaterialButton btnHistory;
    private ImageButton fabEdit;
    private ImageButton fabSettings;
    private ImageButton fabLogout;

    @Nullable
    @Override
    /**
     * Inflate le layout du fragment et prépare les vues.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    /**
     * Appelé après que la vue du fragment soit créée.
     */
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        bindViews(view);
        setupListeners();
        observeViewModel();
        checkAndApplySecretAvatar();

        viewModel.loadUserData();
    }

    /**
     * Lie les vues du layout à leurs références Java.
     */
    private void bindViews(View view) {
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        tvProfileDob = view.findViewById(R.id.tvProfileDob);
        ivProfileAvatar = view.findViewById(R.id.ivProfileAvatar);
        btnPreferences = view.findViewById(R.id.btnPreferences);
        btnHistory = view.findViewById(R.id.btnHistory);
        fabEdit = view.findViewById(R.id.fabEdit);
        fabSettings = view.findViewById(R.id.fabSettings);
        fabLogout = view.findViewById(R.id.fabLogout);
    }

    /**
     * Vérifie si l'utilisateur a sélectionné le thème secret et applique l'avatar correspondant.
     */
    private void checkAndApplySecretAvatar() {
        int currentThemeCode = SessionManager.getInstance().getTheme();

        if (currentThemeCode == 100) {
            ivProfileAvatar.setImageResource(R.drawable.ic_jinx);
            ivProfileAvatar.setImageTintList(null);
        } else {
            ivProfileAvatar.setImageResource(R.drawable.ic_person);
        }
    }

    /**
     * Configure les écouteurs d'événements pour les vues.
     */
    private void setupListeners() {
        fabEdit.setOnClickListener(v -> {
            HapticUtil.playConfirm(v);
            onEditClicked();
        });
        fabSettings.setOnClickListener(v -> {
            HapticUtil.playConfirm(v);
            onSettingsClicked();
        });
        fabLogout.setOnClickListener(v -> {
            HapticUtil.playConfirm(v);
            onLogoutClicked();
        });
        btnPreferences.setOnClickListener(v -> {
            HapticUtil.playConfirm(v);
            onPreferencesClicked();
        });
        btnHistory.setOnClickListener(v -> {
            HapticUtil.playConfirm(v);
            onHistoryClicked();
        });
    }

    /**
     * Observe les LiveData du ViewModel pour mettre à jour l'interface utilisateur en conséquence.
     */
    private void observeViewModel() {
        viewModel.getUserName().observe(getViewLifecycleOwner(), name -> tvProfileName.setText(name != null ? name : "-"));
        viewModel.getUserEmail().observe(getViewLifecycleOwner(), email -> tvProfileEmail.setText(email != null ? email : "-"));
        viewModel.getUserDob().observe(getViewLifecycleOwner(), dob -> tvProfileDob.setText(dob != null ? dob : "-"));
    }

    private void onEditClicked() {
        //Amélioration possible : ajouter une animation de transition vers l'écran d'édition du profil
    }

    /**
     * Navigue vers l'écran des préférences de l'utilisateur.
     */
    private void onPreferencesClicked() {
        if (navController != null) {
            navController.navigate(R.id.action_profile_to_preferences);
        }
    }

    /**
     * Navigue vers l'écran de l'historique des commandes de l'utilisateur.
     */
    private void onHistoryClicked() {
        if (navController != null) {
            navController.navigate(R.id.action_profile_to_history);
        }
    }

    /**
     * Navigue vers l'écran des paramètres de l'utilisateur.
     */
    private void onSettingsClicked() {
        if (navController != null) {
            navController.navigate(R.id.action_profile_to_settings);
        }
    }

    /**
     * Gère le clic sur le bouton de déconnexion.
     */
    private void onLogoutClicked() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.profile_logout_confirm_title)
                .setMessage(R.string.profile_logout_confirm_message)
                .setPositiveButton(R.string.profile_logout, (dialog, which) -> {

                    // On vide la session proprement
                    viewModel.logout();

                    // SÉCURITÉ : on s'assure que le fragment est toujours attaché avant de naviguer
                    if (isAdded() && navController != null) {
                        navController.navigate(R.id.action_profile_to_auth);
                    }
                })
                .setNegativeButton(R.string.error_cancel, null)
                .show();
    }
}