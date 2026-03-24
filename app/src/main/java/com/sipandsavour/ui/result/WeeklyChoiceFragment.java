package com.sipandsavour.ui.result;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.sipandsavour.R;
import com.sipandsavour.data.dto.WineDto;
import com.sipandsavour.util.SlideBackUtil;

public class WeeklyChoiceFragment extends Fragment {

    // Views
    private TextView tvCepage;
    private TextView tvDescription;
    private TextView tvType;
    private TextView tvMealName;
    private TextView tvIngredients;

    private NavController navController;
    private WeeklyChoiceViewModel viewModel;

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
        viewModel = new ViewModelProvider(this).get(WeeklyChoiceViewModel.class);

        bindViews(view);
        observeViewModel();

        viewModel.loadRecommendation();

        View scrollView = view.findViewById(R.id.nestedScrollView);
        SlideBackUtil.attach(() -> navController.popBackStack(), view, scrollView);
    }

    private void bindViews(View view) {
        tvCepage = view.findViewById(R.id.tvCepage);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvType = view.findViewById(R.id.tvType);
        tvMealName = view.findViewById(R.id.tvMealName);
        tvIngredients = view.findViewById(R.id.tvIngredients);
    }

    private void observeViewModel() {
        viewModel.getRecommendationState().observe(getViewLifecycleOwner(), state -> {
            if (state.isLoading()) {
                // CORRECTION: Textes de chargement traduits
                if (tvCepage != null) tvCepage.setText(getString(R.string.weekly_loading_title));
                if (tvDescription != null) tvDescription.setText(getString(R.string.weekly_loading_desc));
                if (tvType != null) tvType.setText("");
            } else if (state.isSuccess() && state.getData() != null) {
                displayWine(state.getData());
            } else if (state.isError()) {
                Toast.makeText(requireContext(), state.getMessage(), Toast.LENGTH_LONG).show();
                // CORRECTION: Utilisation de error_title ("Oups !")
                if (tvCepage != null) tvCepage.setText(getString(R.string.error_title));
                if (tvDescription != null) tvDescription.setText(state.getMessage());
            }
        });
    }

    private void displayWine(WineDto wine) {
        // CORRECTION: Remplacements par getString() pour les inconnus
        if (tvCepage != null) {
            tvCepage.setText(wine.getTitle() != null ? wine.getTitle() :
                    (wine.getVariety() != null ? wine.getVariety() : getString(R.string.result_unknown_wine)));
        }

        if (tvDescription != null) {
            tvDescription.setText(wine.getDescription() != null ? wine.getDescription() : getString(R.string.result_no_description));
        }

        if (tvType != null) tvType.setText(wine.getColorDisplayName());

        // CORRECTION: Textes par défaut des recettes
        if (tvMealName != null) tvMealName.setText(getString(R.string.weekly_pairing));
        if (tvIngredients != null) tvIngredients.setText(getString(R.string.weekly_coming_soon));
    }
}