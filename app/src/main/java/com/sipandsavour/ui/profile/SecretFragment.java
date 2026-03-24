package com.sipandsavour.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.sipandsavour.R;
import com.sipandsavour.data.SessionManager;
import com.sipandsavour.util.HapticUtil;

public class SecretFragment extends Fragment {

    // On utilise 100 comme code pour le thème Jinx
    private static final int THEME_JINX_CODE = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_secret, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SwitchMaterial switchJinx = view.findViewById(R.id.switchJinxTheme);

        // On vérifie si le thème enregistré dans la session est le 100
        boolean isJinxActive = SessionManager.getInstance().getTheme() == THEME_JINX_CODE;
        switchJinx.setChecked(isJinxActive);

        switchJinx.setOnCheckedChangeListener((buttonView, isChecked) -> {
            HapticUtil.playConfirm(view);

            if (isChecked) {
                // On écrase le thème par défaut avec le code 100
                SessionManager.getInstance().setTheme(THEME_JINX_CODE);
            } else {
                // On remet le thème système par défaut si on désactive
                SessionManager.getInstance().setTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }

            // On redémarre l'activité pour appliquer les nouvelles couleurs !
            requireActivity().recreate();
        });
    }
}