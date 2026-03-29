package com.sipandsavour.ui.result;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.sipandsavour.R;
import com.sipandsavour.data.Repository;
import com.sipandsavour.data.dto.WineDto;
import com.sipandsavour.util.HapticUtil;

public class WineDetailsBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_WINE = "wine";
    private WineDto wine;

    private TextView tvWineTitle;
    private TextView tvWineType;
    private TextView tvWineVariety;
    private TextView tvWineDescription;
    private MaterialButton btnAddToFavorites;
    private ImageButton btnCloseWine;

    private boolean isFavorite = false;

    public static WineDetailsBottomSheetFragment newInstance(WineDto wine) {
        WineDetailsBottomSheetFragment fragment = new WineDetailsBottomSheetFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_WINE, wine);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            wine = (WineDto) getArguments().getSerializable(ARG_WINE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_wine_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        displayWineDetails();
        setupListeners();
    }

    private void initViews(View view) {
        tvWineTitle = view.findViewById(R.id.tvWineTitle);
        tvWineType = view.findViewById(R.id.tvWineType);
        tvWineVariety = view.findViewById(R.id.tvWineVariety);
        tvWineDescription = view.findViewById(R.id.tvWineDescription);
        btnAddToFavorites = view.findViewById(R.id.btnAddToFavorites);
        btnCloseWine = view.findViewById(R.id.btnCloseWine);
    }

    private void displayWineDetails() {
        if (wine == null) {
            dismiss();
            return;
        }

        if (tvWineTitle != null) {
            tvWineTitle.setText(wine.getTitle() != null ? wine.getTitle() : getString(R.string.result_unknown_wine));
        }

        if (tvWineType != null) {
            tvWineType.setText(wine.getColorDisplayName());
        }

        if (tvWineVariety != null) {
            tvWineVariety.setText(wine.getVariety() != null ? wine.getVariety() : "-");
        }

        if (tvWineDescription != null) {
            tvWineDescription.setText(wine.getDescription() != null ? wine.getDescription() : getString(R.string.result_no_description));
        }
    }

    private void setupListeners() {
        if (btnCloseWine != null) {
            btnCloseWine.setOnClickListener(v -> dismiss());
        }

        if (btnAddToFavorites != null) {
            btnAddToFavorites.setOnClickListener(v -> {
                HapticUtil.playConfirm(v);
                toggleFavorite();
            });
        }
    }

    private void toggleFavorite() {
        if (wine == null) return;

        if (!isFavorite) {
            Repository.getInstance().addFavorite(wine.getId()).observe(getViewLifecycleOwner(), state -> {
                if (state.isSuccess()) {
                    isFavorite = true;
                    updateFavoriteButton();
                    Toast.makeText(requireContext(), getString(R.string.result_added_snackbar), Toast.LENGTH_SHORT).show();
                } else if (state.isError()) {
                    Toast.makeText(requireContext(), state.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Repository.getInstance().removeFavorite(wine.getId()).observe(getViewLifecycleOwner(), state -> {
                if (state.isSuccess()) {
                    isFavorite = false;
                    updateFavoriteButton();
                    Toast.makeText(requireContext(), getString(R.string.result_removed_snackbar), Toast.LENGTH_SHORT).show();
                } else if (state.isError()) {
                    Toast.makeText(requireContext(), state.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateFavoriteButton() {
        if (btnAddToFavorites == null) return;

        if (isFavorite) {
            btnAddToFavorites.setText(getString(R.string.result_remove_favorite));
            btnAddToFavorites.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_heart_filled));
        } else {
            btnAddToFavorites.setText(getString(R.string.result_add_favorite));
            btnAddToFavorites.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_heart_outline));
        }
    }
}