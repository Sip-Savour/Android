package com.sipnsavour.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WeeklyChoiceActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvCepage, tvDescription, tvType;
    private TextView tvDish, tvIngredients;
    private CardView cvWineCard, cvDishCard;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_choice);

        initializeViews();
        setupClickListeners();
        loadWeeklyData();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        tvCepage = findViewById(R.id.tv_cepage);
        tvDescription = findViewById(R.id.tv_description);
        tvType = findViewById(R.id.tv_type);
        tvDish = findViewById(R.id.tv_dish);
        tvIngredients = findViewById(R.id.tv_ingredients);
        cvWineCard = findViewById(R.id.cv_wine_card);
        cvDishCard = findViewById(R.id.cv_dish_card);
        bottomNav = findViewById(R.id.bottom_navigation);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cvWineCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeeklyChoiceActivity.this, WineInfoActivity.class);
                // Passer les données du vin
                startActivity(intent);
            }
        });
    }

    private void loadWeeklyData() {
        // Charger les données de la semaine depuis une API ou base de données
        tvCepage.setText("Cépage: Shiraz");
        tvDescription.setText("XXXX\nXXXXXXX\nXXXXXXXXX\nXXXXXXXX\nXXXXX\nXX");
        tvType.setText("Type: Rouge");
        tvDish.setText("Plat: Côte de boeuf");
        tvIngredients.setText("Ingredient:\nXXXXX\nXXXXXX\nXXXXXX\nXXXXXX");
    }
}