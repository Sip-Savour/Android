package com.sipandsavour.ui.result;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.sipandsavour.R;

/**
 * Fragment affichant la suggestion hebdomadaire (vin + plat).
 */
public class WeeklyChoiceFragment extends Fragment {

    // Views
    private ImageButton btnBack;
    private TextView tvHeaderTitle;
    private TextView tvCepage;
    private TextView tvDescription;
    private TextView tvType;
    private TextView tvMealName;
    private TextView tvIngredients;

    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weekly_choice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);

        bindViews(view);
        setupHeader();
        loadWeeklySuggestion();
    }

    private void bindViews(View view) {
        // Header
        View headerLayout = view.findViewById(R.id.appBarLayout);
        if (headerLayout != null) {
            btnBack = headerLayout.findViewById(R.id.btnBack);
            tvHeaderTitle = headerLayout.findViewById(R.id.tvHeaderTitle);
        }

        // Wine info
        tvCepage = view.findViewById(R.id.tvCepage);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvType = view.findViewById(R.id.tvType);

        // Meal info
        tvMealName = view.findViewById(R.id.tvMealName);
        tvIngredients = view.findViewById(R.id.tvIngredients);
    }

    private void setupHeader() {
        if (tvHeaderTitle != null) {
            tvHeaderTitle.setText(R.string.weekly_title);
        }

        if (btnBack != null) {
            btnBack.setVisibility(View.VISIBLE);
            btnBack.setOnClickListener(v -> navController.navigateUp());
        }
    }

    private void loadWeeklySuggestion() {
        // TODO: Charger la suggestion hebdomadaire via Repository
        // TODO: Mettre à jour les TextViews avec les données

        // Données de test
        if (tvCepage != null) tvCepage.setText("Shiraz");
        if (tvDescription != null) tvDescription.setText("Un vin rouge corsé aux arômes de fruits noirs et d'épices.");
        if (tvType != null) tvType.setText("Rouge");
        if (tvMealName != null) tvMealName.setText("Côte de boeuf grillée");
        if (tvIngredients != null) tvIngredients.setText("Boeuf, herbes de Provence, ail, huile d'olive");
    }
}