package com.sipandsavour.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.card.MaterialCardView;
import com.sipandsavour.R;

/**
 * Fragment d'accueil avec les deux cartes principales.
 */
public class HomeFragment extends Fragment {

    private SwipeRefreshLayout swipeRefresh;
    private MaterialCardView cardWeekly;
    private MaterialCardView cardSearch;
    private View shimmerContainer;

    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);

        // TODO: Vérifier si l'utilisateur est connecté via SessionManager
        boolean isLoggedIn = false; // TODO: SessionManager.getInstance().isLoggedIn();

        if (!isLoggedIn) {
            navController.navigate(R.id.nav_auth);
            return;
        }

        bindViews(view);
        setupListeners();
    }

    private void bindViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        cardWeekly = view.findViewById(R.id.cardWeekly);
        cardSearch = view.findViewById(R.id.cardSearch);
        shimmerContainer = view.findViewById(R.id.shimmerContainer);
    }

    private void setupListeners() {
        swipeRefresh.setColorSchemeResources(R.color.primary, R.color.secondary);
        swipeRefresh.setOnRefreshListener(this::onRefresh);

        cardWeekly.setOnClickListener(v -> navigateToWeeklyChoice());
        cardSearch.setOnClickListener(v -> navigateToFlavorSelection());
    }

    private void onRefresh() {
        // TODO: Charger la suggestion hebdomadaire via Repository
        swipeRefresh.postDelayed(() -> {
            if (isAdded()) {
                swipeRefresh.setRefreshing(false);
            }
        }, 1000);
    }

    private void navigateToWeeklyChoice() {
        navController.navigate(R.id.action_home_to_weekly);
    }

    private void navigateToFlavorSelection() {
        navController.navigate(R.id.action_home_to_flavor);
    }
}