package com.sipnsavour.app;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sipnsavour.adapter.WineAdapter;
import com.sipnsavour.model.dto.Wine;

import java.util.ArrayList;
import java.util.List;

public class SuggestionActivity extends AppCompatActivity {

    private ImageView ivBack;
    private RecyclerView rvSuggestions;
    private WineAdapter wineAdapter;
    private List<Wine> wineList;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        initializeViews();
        setupRecyclerView();
        loadSuggestions();
        setupClickListeners();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        rvSuggestions = findViewById(R.id.rv_suggestions);
        bottomNav = findViewById(R.id.bottom_navigation);
    }

    private void setupRecyclerView() {
        wineList = new ArrayList<>();
        wineAdapter = new WineAdapter(this, wineList, new WineAdapter.OnWineClickListener() {
            @Override
            public void onWineClick(Wine wine) {
                // Ouvrir les détails du vin
                // Intent vers WineInfoActivity
            }
        });
        rvSuggestions.setLayoutManager(new LinearLayoutManager(this));
        rvSuggestions.setAdapter(wineAdapter);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
    }

    private void loadSuggestions() {
        // Charger les suggestions basées sur les saveurs sélectionnées
        ArrayList<String> selectedFlavors = getIntent().getStringArrayListExtra("selected_flavors");

        // Simulation de données
        wineList.add(new Wine("Shiraz", "XXXX\nXXXXXXX\nXXXXXXXXX\nXXXXXXXX\nXXXXX\nXX", "Rouge"));
        wineList.add(new Wine("Shiraz", "XXXX\nXXXXXXX\nXXXXXXXXX\nXXXXXXXX\nXXXXX", "Rouge"));
        wineAdapter.notifyDataSetChanged();
    }
}
