package com.sipandsavour.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
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

    private RadioGroup rgLanguage;
    private RadioButton rbFrench, rbEnglish;
    private MaterialButton btnSave;

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
        btnSave = view.findViewById(R.id.btnSaveSettings);

        observeViewModel();
        setupListeners();
    }

    private void observeViewModel() {
        // Cocher le bon bouton radio selon la langue actuelle
        viewModel.getCurrentLanguage().observe(getViewLifecycleOwner(), lang -> {
            if ("en".equals(lang)) {
                rbEnglish.setChecked(true);
            } else {
                rbFrench.setChecked(true);
            }
        });
    }

    private void setupListeners() {
        // Changement temporaire dans le ViewModel lors du clic
        rgLanguage.setOnCheckedChangeListener((group, checkedId) -> {
            HapticUtil.playLightClick(group); // Petite vibration
            if (checkedId == R.id.rbEnglish) {
                viewModel.setLanguage("en");
            } else {
                viewModel.setLanguage("fr");
            }
        });

        // Sauvegarde définitive et application
        btnSave.setOnClickListener(v -> {
            HapticUtil.playConfirm(v); // Grosse vibration

            // 1. Sauvegarde dans SharedPreferences
            viewModel.saveSettings();

            // 2. Application de la nouvelle locale (recrée l'activité)
            String newLang = viewModel.getCurrentLanguage().getValue();
            LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(newLang);
            AppCompatDelegate.setApplicationLocales(appLocale);

            Toast.makeText(requireContext(), getString(R.string.preferences_saved_toast), Toast.LENGTH_SHORT).show();

            // Le retour arrière est géré automatiquement par la recréation de l'activité,
            // mais on l'assure ici au cas où.
            navController.popBackStack();
        });
    }
}