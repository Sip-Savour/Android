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

import com.sipandsavour.R;
import com.sipandsavour.data.dto.WineDto;
import com.sipandsavour.util.HapticUtil;

public class ResultFragment extends Fragment {

    private ResultViewModel viewModel;

    // Wine card views
    private TextView tvTitle;
    private TextView tvCepage;
    private TextView tvDescription;
    private TextView tvType;
    private ImageButton fabFavorite;
    private View wineCard;

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

        viewModel = new ViewModelProvider(this).get(ResultViewModel.class);

        initViews(view);
        setupListeners();
        observeViewModel();
        handleArguments();
    }

    private void initViews(View view) {
        wineCard = view.findViewById(R.id.wineCard);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvCepage = view.findViewById(R.id.tvCepage);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvType = view.findViewById(R.id.tvType);
        fabFavorite = view.findViewById(R.id.fabFavorite);
    }

    private void setupListeners() {
        if (fabFavorite != null) {
            fabFavorite.setOnClickListener(v -> {
                viewModel.toggleFavorite();
                // Utiliser playConfirm au lieu de lightTap
                HapticUtil.playConfirm(v);
                animateFavoriteButton();
            });
        }
    }

    private void observeViewModel() {
        // Observer le vin actuel
        viewModel.getCurrentWine().observe(getViewLifecycleOwner(), wine -> {
            if (wine != null) {
                displayWine(wine);
            }
        });

        // Observer l'état favori
        viewModel.getIsFavorite().observe(getViewLifecycleOwner(), isFavorite -> {
            updateFavoriteIcon(isFavorite != null && isFavorite);
        });
    }

    private void handleArguments() {
        if (getArguments() != null) {
            WineDto wine = (WineDto) getArguments().getSerializable("wine");
            if (wine != null) {
                viewModel.setCurrentWine(wine);
            }
        }
    }

    private void displayWine(WineDto wine) {
        if (wine == null) return;

        // Titre
        if (tvTitle != null) {
            String title = wine.getTitle() != null ? wine.getTitle() : getString(R.string.result_unknown_wine);
            tvTitle.setText(title);
        }

        // Cépage (variety au lieu de cepage)
        if (tvCepage != null) {
            String variety = wine.getVariety();
            if (variety != null && !variety.isEmpty()) {
                tvCepage.setText(variety);
            } else {
                tvCepage.setText("Non spécifié");
            }
        }

        // Description
        if (tvDescription != null) {
            String description = wine.getDescription();
            if (description != null && !description.isEmpty()) {
                tvDescription.setText(description);
            } else {
                tvDescription.setText(getString(R.string.result_no_description));
            }
        }

        // Type (color au lieu de type)
        if (tvType != null) {
            String colorDisplay = wine.getColorDisplayName();
            tvType.setText(colorDisplay);
        }

        // Mise à jour du background selon la couleur
        updateCardBackground(wine.getColor());
    }

    private void updateCardBackground(String wineColor) {
        if (wineCard == null || wineColor == null) return;

        switch (wineColor.toLowerCase()) {
            case "red":
            case "rouge":
                wineCard.setBackgroundResource(R.drawable.bg_card_gradient_rouge);
                break;
            case "white":
            case "blanc":
                wineCard.setBackgroundResource(R.drawable.bg_card_gradient_blanc);
                break;
            case "rose":
            case "rosé":
            default:
                wineCard.setBackgroundResource(R.drawable.bg_card_gradient_rose);
                break;
        }
    }

    private void updateFavoriteIcon(boolean isFavorite) {
        if (fabFavorite != null) {
            fabFavorite.setImageResource(
                isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline
            );
        }
    }

    private void animateFavoriteButton() {
        if (fabFavorite != null) {
            fabFavorite.animate()
                    .scaleX(1.3f)
                    .scaleY(1.3f)
                    .setDuration(100)
                    .withEndAction(() ->
                            fabFavorite.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(100)
                                    .start()
                    )
                    .start();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tvTitle = null;
        tvCepage = null;
        tvDescription = null;
        tvType = null;
        fabFavorite = null;
        wineCard = null;
    }
}