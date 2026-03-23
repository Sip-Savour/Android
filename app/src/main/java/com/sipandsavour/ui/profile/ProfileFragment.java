package com.sipandsavour.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.sipandsavour.ui.auth.AuthViewModel;
import com.sipandsavour.util.HapticUtil;

public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private NavController navController;

    // Views
    private TextView tvProfileName;
    private TextView tvProfileEmail;
    private TextView tvProfileDob;
    private MaterialButton btnPreferences;
    private MaterialButton btnHistory;
    private ImageButton fabEdit;
    private ImageButton fabSettings;
    private ImageButton fabLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        bindViews(view);
        setupListeners();
        observeViewModel();

        viewModel.loadUserData();
    }

    private void bindViews(View view) {
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        tvProfileDob = view.findViewById(R.id.tvProfileDob);
        btnPreferences = view.findViewById(R.id.btnPreferences);
        btnHistory = view.findViewById(R.id.btnHistory);
        fabEdit = view.findViewById(R.id.fabEdit);
        fabSettings = view.findViewById(R.id.fabSettings);
        fabLogout = view.findViewById(R.id.fabLogout);
    }

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

    private void observeViewModel() {
        viewModel.getUserName().observe(getViewLifecycleOwner(), name -> tvProfileName.setText(name != null ? name : "-"));

        viewModel.getUserEmail().observe(getViewLifecycleOwner(), email -> tvProfileEmail.setText(email != null ? email : "-"));

        viewModel.getUserDob().observe(getViewLifecycleOwner(), dob -> tvProfileDob.setText(dob != null ? dob : "-"));
    }

    private void onEditClicked() {
        // TODO: Naviguer vers l'écran d'édition de profil
    }

    private void onSettingsClicked() {
        // TODO: Naviguer vers les paramètres
    }

    private void onPreferencesClicked() {
        if (navController != null) {
            navController.navigate(R.id.action_profile_to_preferences);
        }
    }

    private void onHistoryClicked() {
        if (navController != null) {
            navController.navigate(R.id.action_profile_to_history);
        }
    }

    private void onLogoutClicked() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.profile_logout_confirm_title)
                .setMessage(R.string.profile_logout_confirm_message)
                .setPositiveButton(R.string.profile_logout, (dialog, which) -> {
                    viewModel.logout();
                    navController.navigate(R.id.action_profile_to_auth);
                })
                .setNegativeButton(R.string.error_cancel, null)
                .show();
    }
}