package com.sipnsavour.app;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class PreferencesActivity extends AppCompatActivity {

    private ImageView ivBack;
    private ChipGroup chipGroupWineTypes, chipGroupFlavors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        chipGroupWineTypes = findViewById(R.id.chip_group_wine_types);
        chipGroupFlavors = findViewById(R.id.chip_group_flavors);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
    }
}