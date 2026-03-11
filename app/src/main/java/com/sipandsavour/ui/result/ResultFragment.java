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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sipandsavour.R;
import com.sipandsavour.data.dto.WineDto;

import java.util.List;

/**
 * Fragment affichant le détail d'un vin recommandé.
 */
public class ResultFragment extends Fragment {

    private ResultViewModel viewModel;
    private NavController navController;

    // Views
    private ImageButton btnBack;
    private TextView tvHeaderTitle;
    private TextView tvCepage;
    private TextView tvDescription;
    private TextView tvType;
    private ChipGroup chipGroupKeywords;
    private FloatingActionButton fabFavorite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);
        viewModel = new ViewModelProvider(this).get(ResultViewModel.class);

        bindViews(view);
        setupHeader();
        setupFavoriteButton();
        observeViewModel();

        // TODO: Récupérer le vin depuis les arguments ou SelectionViewModel
    }

    private void bindViews(View view) {
        View headerLayout = view.findViewById(R.id.appBarLayout);
        if (headerLayout != null) {
            btnBack = headerLayout.findViewById(R.id.btnBack);
            tvHeaderTitle = headerLayout.findViewById(R.id.tvHeaderTitle);
        }

        tvCepage = view.findViewById(R.id.tvCepage);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvType = view.findViewById(R.id.tvType);
        chipGroupKeywords = view.findViewById(R.id.chipGroupKeywords);
        fabFavorite = view.findViewById(R.id.fabFavorite);
    }

    private void setupHeader() {
        if (tvHeaderTitle != null) {
            tvHeaderTitle.setText(R.string.info_title);
        }

        if (btnBack != null) {
            btnBack.setVisibility(View.VISIBLE);
            btnBack.setOnClickListener(v -> navController.navigateUp());
        }
    }

    private void setupFavoriteButton() {
        fabFavorite.setOnClickListener(v -> {
            viewModel.toggleFavorite();
        });
    }

    private void observeViewModel() {
        viewModel.getCurrentWine().observe(getViewLifecycleOwner(), this::displayWine);

        viewModel.getIsFavorite().observe(getViewLifecycleOwner(), isFavorite -> {
            updateFavoriteIcon(isFavorite);
            // TODO: Afficher snackbar selon l'action
        });
    }

    private void displayWine(WineDto wine) {
        if (wine == null) return;

        tvCepage.setText(wine.getVariety() != null ? wine.getVariety() : "-");
        tvDescription.setText(wine.getDescription() != null ? wine.getDescription() : "-");
        tvType.setText(wine.getColorDisplayName());

        displayKeywords(wine.getKeywords());
    }

    private void displayKeywords(List<String> keywords) {
        chipGroupKeywords.removeAllViews();

        if (keywords == null || keywords.isEmpty()) return;

        for (String keyword : keywords) {
            Chip chip = new Chip(requireContext());
            chip.setText(keyword);
            chip.setClickable(false);
            chip.setCheckable(false);
            chip.setChipBackgroundColorResource(R.color.surface_variant);
            chip.setTextColor(getResources().getColor(R.color.primary, null));

            chipGroupKeywords.addView(chip);
        }
    }

    private void updateFavoriteIcon(boolean isFavorite) {
        fabFavorite.setImageResource(
                isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline
        );
    }

    private void showSnackbar(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }
}