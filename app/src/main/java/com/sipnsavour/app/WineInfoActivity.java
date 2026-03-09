package com.sipnsavour.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sipnsavour.model.dto.Wine;

public class WineInfoActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvCepage, tvDescription, tvType, tvTags;
    private FloatingActionButton fabFavorite;
    private boolean isFavorite = false;
    private Wine currentWine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wine_info);

        initializeViews();
        loadWineData();
        setupClickListeners();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        tvCepage = findViewById(R.id.tv_cepage);
        tvDescription = findViewById(R.id.tv_description);
        tvType = findViewById(R.id.tv_type);
        tvTags = findViewById(R.id.tv_tags);
        fabFavorite = findViewById(R.id.fab_favorite);
    }

    private void loadWineData() {
        // Récupérer les données du vin depuis Intent ou base de données
        tvCepage.setText("Cépage: Shiraz");
        tvDescription.setText("XXXX\nXXXXXXX\nXXXXXXXXX\nXXXXXXXXX\nXXXXXXXX\nXXXXX\nXX");
        tvType.setText("Type: Rouge");
        tvTags.setText("Mots-clefs:\ntag, tag, tag");
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        fabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite();
            }
        });
    }

    private void toggleFavorite() {
        isFavorite = !isFavorite;
        if (isFavorite) {
            fabFavorite.setImageResource(R.drawable.ic_favorite_filled);
            // Ajouter aux favoris
        } else {
            fabFavorite.setImageResource(R.drawable.ic_favorite_outline);
            // Retirer des favoris
        }
    }
}
