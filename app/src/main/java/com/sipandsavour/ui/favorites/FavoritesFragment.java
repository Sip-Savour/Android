package com.sipandsavour.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.sipandsavour.R;
import com.sipandsavour.data.dto.WineDto;
import com.sipandsavour.ui.result.ResultViewModel;

public class FavoritesFragment extends Fragment implements FavoritesAdapter.OnFavoriteClickListener {

    private FavoritesViewModel viewModel;
    private ResultViewModel resultViewModel;
    private NavController navController;

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvFavorites;
    private LinearLayout layoutEmpty;
    private LinearLayout shimmerFavorites;

    private FavoritesAdapter adapter;

    /**
     * Crée la vue du fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    /**
     * Initialise les vues et configure les écouteurs.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);
        viewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        resultViewModel = new ViewModelProvider(requireActivity()).get(ResultViewModel.class);

        bindViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        setupSwipeToDelete();
        observeViewModel();

        viewModel.loadFavorites();
    }

    /**
     * Lie les vues du layout aux variables de la classe.
     */
    private void bindViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        rvFavorites = view.findViewById(R.id.rvFavorites);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        shimmerFavorites = view.findViewById(R.id.shimmerFavorites);
    }

    /**
     * Configure le RecyclerView pour afficher les favoris.
     */
    private void setupRecyclerView() {
        adapter = new FavoritesAdapter();
        adapter.setOnFavoriteClickListener(this);
        rvFavorites.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFavorites.setAdapter(adapter);
    }

    /**
     * Configure le SwipeRefreshLayout.
     */
    private void setupSwipeRefresh() {
        swipeRefresh.setColorSchemeResources(R.color.primary, R.color.secondary);
        swipeRefresh.setOnRefreshListener(() -> viewModel.refresh());
    }

    /**
     * Configure le swipe pour supprimer les favoris.
     */
    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                viewModel.removeFavorite(position);
                showUndoSnackbar();
            }
        };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(rvFavorites);
    }

    /**
     * Observe les LiveData du ViewModel pour mettre à jour l'interface utilisateur.
     */
    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            swipeRefresh.setRefreshing(false);
            shimmerFavorites.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getIsEmpty().observe(getViewLifecycleOwner(), isEmpty -> {
            layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            rvFavorites.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        });

        viewModel.getFavorites().observe(getViewLifecycleOwner(),
                favorites -> adapter.submitList(favorites));
    }

    /**
     * Affiche une Snackbar pour annuler la suppression d'un favori.
     */
    private void showUndoSnackbar() {
        if (getView() == null) return;
        Snackbar.make(getView(), R.string.favorites_removed_snackbar, Snackbar.LENGTH_LONG)
                .setAction(R.string.favorites_undo, v -> viewModel.undoRemove())
                .setActionTextColor(getResources().getColor(R.color.secondary, null))
                .show();
    }

    /**
     * Gère le clic sur un favori.
     */
    @Override
    public void onFavoriteClick(WineDto wine, int position) {
        resultViewModel.setCurrentWine(wine);
        navController.navigate(R.id.action_favorites_to_wineDetail);
    }
}