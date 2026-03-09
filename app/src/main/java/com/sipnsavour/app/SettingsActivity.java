package com.sipnsavour.app;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    private ImageView ivBack;
    private SwitchCompat switchNotifications, switchDarkMode, switchAutoUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        switchNotifications = findViewById(R.id.switch_notifications);
        switchDarkMode = findViewById(R.id.switch_dark_mode);
        switchAutoUpdate = findViewById(R.id.switch_auto_update);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: Sauvegarder les préférences
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: Changer le thème
        });

        switchAutoUpdate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: Sauvegarder les préférences
        });
    }
}
