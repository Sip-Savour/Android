package com.sipnsavour.app;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.sipnsavour.adapter.WineAdapter;
import com.sipnsavour.model.dto.Wine;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ImageView ivBack;
    private RecyclerView rvHistory;
    private WineAdapter wineAdapter;
    private List<Wine> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        loadHistory();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        rvHistory = findViewById(R.id.rv_history);
    }

    private void setupRecyclerView() {
        historyList = new ArrayList<>();
        wineAdapter = new WineAdapter(this, historyList, wine -> {
            // Ouvrir les détails du vin
        });
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(wineAdapter);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
    }

    private void loadHistory() {
        // TODO: Charger l'historique depuis la base de données
        historyList.add(new Wine("Château Margaux", "Consulté le 15/12/2024", "Rouge"));
        historyList.add(new Wine("Chablis", "Consulté le 14/12/2024", "Blanc"));
        wineAdapter.notifyDataSetChanged();
    }
}