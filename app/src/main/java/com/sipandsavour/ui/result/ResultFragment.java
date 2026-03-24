package com.sipandsavour.ui.result;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import com.google.android.material.snackbar.Snackbar;
import com.sipandsavour.R;
import com.sipandsavour.data.dto.WineDto;
import com.sipandsavour.util.HapticUtil;

public class ResultFragment extends Fragment {

    private ResultViewModel viewModel;
    private NavController navController;

    // Views
    private TextView tvTitle;
    private TextView tvCepage;
    private TextView tvDescription;
    private TextView tvType;
    private ImageButton fabFavorite;

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
        viewModel = new ViewModelProvider(requireActivity()).get(ResultViewModel.class);

        bindViews(view);
        setupFavoriteButton();
        observeViewModel();

        setupSwipeGesture(view);
    }

    private void bindViews(View view) {
        tvTitle = view.findViewById(R.id.tvTitle);
        tvCepage = view.findViewById(R.id.tvCepage);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvType = view.findViewById(R.id.tvType);
        fabFavorite = view.findViewById(R.id.fabFavorite);
    }

    private void setupFavoriteButton() {
        if (fabFavorite != null) {
            fabFavorite.setOnClickListener(v -> {
                HapticUtil.playConfirm(v);
                viewModel.toggleFavorite();
            });
        }
    }

    private void observeViewModel() {
        viewModel.getCurrentWine().observe(getViewLifecycleOwner(), this::displayWine);
        viewModel.getIsFavorite().observe(getViewLifecycleOwner(), this::updateFavoriteIcon);
    }

    private void displayWine(WineDto wine) {
        if (wine == null) return;

        View card = getView() != null ? getView().findViewById(R.id.wineCard) : null;
        if (card != null) {
            card.setAlpha(0f);
            card.setTranslationX(150f);
            card.animate().alpha(1f).translationX(0f).setDuration(250).start();
        }

        // CORRECTION: getString pour le titre et les valeurs par défaut
        if (tvTitle != null) tvTitle.setText(wine.getTitle() != null ? wine.getTitle() : getString(R.string.result_unknown_wine));
        tvCepage.setText(wine.getVariety() != null ? wine.getVariety() : "-");
        tvDescription.setText(wine.getDescription() != null ? wine.getDescription() : "-");
        tvType.setText(wine.getColorDisplayName());
    }

    private void updateFavoriteIcon(boolean isFavorite) {
        if (fabFavorite != null) {
            fabFavorite.setImageResource(
                    isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline
            );

            if (isFavorite) {
                fabFavorite.setColorFilter(android.graphics.Color.parseColor("#E53935"));
            } else {
                fabFavorite.setColorFilter(android.graphics.Color.parseColor("#FFFFFF"));
            }
        }
    }

    private void showSnackbar(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupSwipeGesture(View view) {
        View.OnTouchListener invincibleSwipeListener = new View.OnTouchListener() {
            private float startX = 0;
            private float startY = 0;
            private boolean isSwiping = false;
            private static final int SWIPE_THRESHOLD = 120;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        startY = event.getRawY();
                        isSwiping = false;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float diffX = event.getRawX() - startX;
                        float diffY = event.getRawY() - startY;

                        if (!isSwiping && Math.abs(diffX) > 40 && Math.abs(diffX) > Math.abs(diffY)) {
                            isSwiping = true;
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        }

                        if (isSwiping) return true;
                        break;

                    case MotionEvent.ACTION_UP:
                        if (isSwiping) {
                            float finalDiffX = event.getRawX() - startX;
                            if (Math.abs(finalDiffX) > SWIPE_THRESHOLD) {
                                if (finalDiffX > 0) {
                                    navController.popBackStack();
                                } else {
                                    boolean hasNext = viewModel.nextWine();
                                    if (!hasNext) {
                                        // CORRECTION: On prévient avec la bonne langue
                                        showSnackbar(getString(R.string.result_last_wine));
                                    }
                                }
                            }
                            isSwiping = false;
                            return true;
                        }
                        break;
                }
                return false;
            }
        };

        View scrollView = view.findViewById(R.id.scrollView);
        if (scrollView != null) scrollView.setOnTouchListener(invincibleSwipeListener);
        view.setOnTouchListener(invincibleSwipeListener);
    }
}