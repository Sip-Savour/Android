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

        // On active le slide indestructible !
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

        // ANIMATION VISUELLE DU SLIDE
        View card = getView() != null ? getView().findViewById(R.id.wineCard) : null;
        if (card != null) {
            card.setAlpha(0f);
            card.setTranslationX(150f); // Démarre décalé vers la droite
            card.animate().alpha(1f).translationX(0f).setDuration(250).start(); // Glisse vers le centre
        }

        if (tvTitle != null) tvTitle.setText(wine.getTitle() != null ? wine.getTitle() : "Vin Inconnu");
        tvCepage.setText(wine.getVariety() != null ? wine.getVariety() : "-");
        tvDescription.setText(wine.getDescription() != null ? wine.getDescription() : "-");
        tvType.setText(wine.getColorDisplayName());
    }

    private void updateFavoriteIcon(boolean isFavorite) {
        if (fabFavorite != null) {
            // 1. On change la forme de l'icône (Plein ou Vide)
            fabFavorite.setImageResource(
                    isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline
            );

            // 2. On force la couleur de l'icône !
            if (isFavorite) {
                // Si c'est un favori, on applique un beau rouge vif
                fabFavorite.setColorFilter(android.graphics.Color.parseColor("#E53935"));
            } else {
                // Sinon, on remet l'icône en blanc (ou la couleur par défaut de votre thème)
                fabFavorite.setColorFilter(android.graphics.Color.parseColor("#FFFFFF"));
            }
        }
    }

    private void showSnackbar(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }

    // =======================================================
    //  GESTION DU SWIPE (MÉTHODE RAWX ABSOLUE)
    // =======================================================
    @SuppressLint("ClickableViewAccessibility")
    private void setupSwipeGesture(View view) {
        View.OnTouchListener invincibleSwipeListener = new View.OnTouchListener() {
            private float startX = 0;
            private float startY = 0;
            private boolean isSwiping = false;
            private static final int SWIPE_THRESHOLD = 120; // Sensibilité du glissement

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

                        // Si on bouge horizontalement de plus de 40 pixels
                        if (!isSwiping && Math.abs(diffX) > 40 && Math.abs(diffX) > Math.abs(diffY)) {
                            isSwiping = true;
                            // On hurle au ScrollView de ne pas toucher à ce geste !
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        }

                        // Si on est en train de swiper, on "avale" l'événement pour bloquer le défilement haut/bas
                        if (isSwiping) return true;
                        break;

                    case MotionEvent.ACTION_UP:
                        if (isSwiping) {
                            float finalDiffX = event.getRawX() - startX;
                            if (Math.abs(finalDiffX) > SWIPE_THRESHOLD) {
                                if (finalDiffX > 0) {
                                    // Slide vers la DROITE (→) : Retour en arrière
                                    navController.popBackStack();
                                } else {
                                    // Slide vers la GAUCHE (←) : Vin Suivant
                                    boolean hasNext = viewModel.nextWine();
                                    if (!hasNext) {
                                        showSnackbar("C'est le dernier vin de la liste !");
                                    }
                                }
                            }
                            isSwiping = false;
                            return true; // Le swipe est terminé
                        }
                        break;
                }
                return false; // Laisse passer le clic normal et le scroll normal
            }
        };

        // On attache le super-détecteur à la fois au ScrollView et au fond de l'écran
        View scrollView = view.findViewById(R.id.scrollView);
        if (scrollView != null) scrollView.setOnTouchListener(invincibleSwipeListener);
        view.setOnTouchListener(invincibleSwipeListener);
    }
}