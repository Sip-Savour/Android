package com.sipandsavour.ui.result;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.sipandsavour.R;

public class WeeklyChoiceFragment extends Fragment {

    // Views
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
        loadWeeklySuggestion();
    }

    private void bindViews(View view) {
        tvCepage = view.findViewById(R.id.tvCepage);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvType = view.findViewById(R.id.tvType);
        tvMealName = view.findViewById(R.id.tvMealName);
        tvIngredients = view.findViewById(R.id.tvIngredients);
    }

    private void loadWeeklySuggestion() {
        // TODO: Charger la suggestion hebdomadaire via Repository

        // Données de test
        if (tvCepage != null) tvCepage.setText("Shiraz");
        if (tvDescription != null) tvDescription.setText("Un vin rouge corsé aux arômes de fruits noirs et d'épices.");
        if (tvType != null) tvType.setText("Rouge");
        if (tvMealName != null) tvMealName.setText("Côte de boeuf grillée");
        if (tvIngredients != null) tvIngredients.setText("Boeuf, herbes de Provence, ail, huile d'olive");
    }
}