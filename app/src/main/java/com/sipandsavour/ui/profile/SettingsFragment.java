package com.sipandsavour.ui.profile;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.sipandsavour.R;
import com.sipandsavour.util.HapticUtil;

public class SettingsFragment extends Fragment {

    private SettingsViewModel viewModel;
    private NavController navController;

    private RadioGroup rgLanguage, rgTheme;
    private RadioButton rbFrench, rbEnglish;
    private RadioButton rbThemeSystem, rbThemeLight, rbThemeDark;
    private MaterialButton btnSave;

    private com.sipandsavour.util.EasterEggDetector easterEggDetector;

    // GESTIONNAIRE DE PERMISSION POUR LE MICROPHONE
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialisation de la demande de permission
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted && easterEggDetector != null) {
                easterEggDetector.start(); // Si accepté, on allume le micro
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        rgLanguage = view.findViewById(R.id.rgLanguage);
        rbFrench = view.findViewById(R.id.rbFrench);
        rbEnglish = view.findViewById(R.id.rbEnglish);

        rgTheme = view.findViewById(R.id.rgTheme);
        rbThemeSystem = view.findViewById(R.id.rbThemeSystem);
        rbThemeLight = view.findViewById(R.id.rbThemeLight);
        rbThemeDark = view.findViewById(R.id.rbThemeDark);

        btnSave = view.findViewById(R.id.btnSaveSettings);

        observeViewModel();
        setupListeners();

        // Initialisation du détecteur
        easterEggDetector = new com.sipandsavour.util.EasterEggDetector(requireContext());
        easterEggDetector.setListener(() -> {
            if (isAdded() && navController != null && navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.settingsFragment) {
                HapticUtil.playConfirm(requireView());
                easterEggDetector.stop();
                navController.navigate(R.id.action_settings_to_secret);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Vérification de la permission Micro
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            // Permission déjà accordée, on lance tout
            if (easterEggDetector != null) {
                easterEggDetector.start();
            }
        } else {
            // On demande la permission (une popup va apparaître)
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }
    }

    @Override
    public void onPause() {
        if (easterEggDetector != null) {
            easterEggDetector.stop();
        }
        super.onPause();
    }

    private void observeViewModel() {
        viewModel.getCurrentLanguage().observe(getViewLifecycleOwner(), lang -> {
            if ("en".equals(lang)) {
                rbEnglish.setChecked(true);
            } else {
                rbFrench.setChecked(true);
            }
        });

        viewModel.getCurrentTheme().observe(getViewLifecycleOwner(), theme -> {
            if (theme == AppCompatDelegate.MODE_NIGHT_NO) {
                rbThemeLight.setChecked(true);
            } else if (theme == AppCompatDelegate.MODE_NIGHT_YES) {
                rbThemeDark.setChecked(true);
            } else {
                rbThemeSystem.setChecked(true);
            }
        });
    }

    private void setupListeners() {
        rgLanguage.setOnCheckedChangeListener((group, checkedId) -> {
            HapticUtil.playLightClick(group);
            if (checkedId == R.id.rbEnglish) {
                viewModel.setLanguage("en");
            } else {
                viewModel.setLanguage("fr");
            }
        });

        rgTheme.setOnCheckedChangeListener((group, checkedId) -> {
            HapticUtil.playLightClick(group);
            if (checkedId == R.id.rbThemeLight) {
                viewModel.setTheme(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (checkedId == R.id.rbThemeDark) {
                viewModel.setTheme(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                viewModel.setTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        });

        btnSave.setOnClickListener(v -> {
            HapticUtil.playConfirm(v);
            viewModel.saveSettings();

            if (viewModel.getCurrentTheme().getValue() != null) {
                AppCompatDelegate.setDefaultNightMode(viewModel.getCurrentTheme().getValue());
            }

            String newLang = viewModel.getCurrentLanguage().getValue();
            LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(newLang);
            AppCompatDelegate.setApplicationLocales(appLocale);

            Toast.makeText(requireContext(), getString(R.string.preferences_saved_toast), Toast.LENGTH_SHORT).show();
            navController.popBackStack();
        });
    }
}