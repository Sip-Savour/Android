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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sipandsavour.R;

/**
 * Fragment affichant le profil utilisateur.
 */
public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private NavController navController;

    // Views
    private ImageButton btnBack;
    private TextView tvHeaderTitle;
    private TextView tvProfileName;
    private TextView tvProfileEmail;
    private TextView tvProfileDob;
    private MaterialButton btnPreferences;
    private MaterialButton btnHistory;
    private FloatingActionButton fabEdit;
    private FloatingActionButton fabSettings;
    private FloatingActionButton fabLogout;

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
        setupHeader();
        setupListeners();
        observeViewModel();

        viewModel.loadUserData();
    }

    private void bindViews(View view) {
        View headerLayout = view.findViewById(R.id.appBarLayout);
        if (headerLayout != null) {
            btnBack = headerLayout.findViewById(R.id.btnBack);
            tvHeaderTitle = headerLayout.findViewById(R.id.tvHeaderTitle);
        }

        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        tvProfileDob = view.findViewById(R.id.tvProfileDob);
        btnPreferences = view.findViewById(R.id.btnPreferences);
        btnHistory = view.findViewById(R.id.btnHistory);
        fabEdit = view.findViewById(R.id.fabEdit);
        fabSettings = view.findViewById(R.id.fabSettings);
        fabLogout = view.findViewById(R.id.fabLogout);
    }

    private void setupHeader() {
        if (tvHeaderTitle != null) {
            tvHeaderTitle.setText(R.string.profile_account_title);
        }

        if (btnBack != null) {
            btnBack.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        fabEdit.setOnClickListener(v -> onEditClicked());
        fabSettings.setOnClickListener(v -> onSettingsClicked());
        fabLogout.setOnClickListener(v -> onLogoutClicked());
        btnPreferences.setOnClickListener(v -> onPreferencesClicked());
        btnHistory.setOnClickListener(v -> onHistoryClicked());
    }

    private void observeViewModel() {
        viewModel.getUserName().observe(getViewLifecycleOwner(), name -> {
            tvProfileName.setText(name != null ? name : "-");
        });

        viewModel.getUserEmail().observe(getViewLifecycleOwner(), email -> {
            tvProfileEmail.setText(email != null ? email : "-");
        });

        viewModel.getUserDob().observe(getViewLifecycleOwner(), dob -> {
            tvProfileDob.setText(dob != null ? dob : "-");
        });
    }

    private void onEditClicked() {
        // TODO: Naviguer vers l'écran d'édition de profil
    }

    private void onSettingsClicked() {
        // TODO: Naviguer vers les paramètres
    }

    private void onPreferencesClicked() {
        // TODO: Naviguer vers les préférences de vins
    }

    private void onHistoryClicked() {
        // TODO: Naviguer vers l'historique des recherches
    }

    private void onLogoutClicked() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.profile_logout_confirm_title)
                .setMessage(R.string.profile_logout_confirm_message)
                .setPositiveButton(R.string.profile_logout, (dialog, which) -> {
                    viewModel.logout();
                    // TODO: Naviguer vers l'écran de connexion
                })
                .setNegativeButton(R.string.error_cancel, null)
                .show();
    }
}