package com.sipandsavour.ui.result;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sipandsavour.R;
import com.sipandsavour.data.dto.BottleResponse;
import com.sipandsavour.data.dto.WineDto;
import com.sipandsavour.ui.selection.SelectionViewModel;
import com.sipandsavour.util.SlideBackUtil;
import com.sipandsavour.util.TranslationManager;

import java.util.ArrayList;
import java.util.List;

public class SuggestionListFragment extends Fragment implements SuggestionAdapter.OnSuggestionClickListener {

    private NavController navController;
    private ResultViewModel resultViewModel;
    private SelectionViewModel selectionViewModel;

    private RecyclerView rvSuggestions;
    private LinearLayout layoutEmpty;

    private SuggestionAdapter adapter;

    @Nullable
    @Override
    /** 
     * Inflate the layout for this fragment
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suggestion_list, container, false);
    }

    @Override
    /**
     * Initialize views, set up RecyclerView, observe ViewModel data, and attach slide-back gesture
     */
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);

        resultViewModel = new ViewModelProvider(requireActivity()).get(ResultViewModel.class);
        selectionViewModel = new ViewModelProvider(requireActivity()).get(SelectionViewModel.class);

        bindViews(view);
        setupRecyclerView();
        observePrediction();
        SlideBackUtil.attach(() -> navController.popBackStack(), view, rvSuggestions);
    }

    /**
     * Bind the views to the fragment.
     * @param view The root view of the fragment.
     */
    private void bindViews(View view) {
        rvSuggestions = view.findViewById(R.id.rvSuggestions);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
    }

    /**
     * Set up the RecyclerView for displaying suggestions.
     */
    private void setupRecyclerView() {
        adapter = new SuggestionAdapter();
        adapter.setOnSuggestionClickListener(this);
        rvSuggestions.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSuggestions.setAdapter(adapter);
    }

    /**
     * Observe the prediction result from the SelectionViewModel.
     */
    private void observePrediction() {
        selectionViewModel.getPredictionResult().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            if (state.isLoading()) {
                rvSuggestions.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
            }
            else if (state.isSuccess() && state.getData() != null) {
                List<BottleResponse> apiBottles = state.getData().getBottle();
                List<WineDto> suggestions = mapApiToWineDto(apiBottles);

                // --- TRADUCTION DE LA LISTE ICI ---
                TranslationManager.getInstance().translateWineListIfNeeded(suggestions, translatedList -> {
                    displaySuggestions(translatedList);
                    triggerSuccessVibration();
                });
            }
            else if (state.isError()) {
                rvSuggestions.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
                // Correction du texte d'erreur en dur
                Toast.makeText(requireContext(), getString(R.string.error_title) + " " + state.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Map API bottle responses to WineDto objects.
     * @param apiBottles The list of API bottle responses.
     * @return The list of WineDto objects.
     */
    private List<WineDto> mapApiToWineDto(List<BottleResponse> apiBottles) {
        List<WineDto> list = new ArrayList<>();
        if (apiBottles == null) return list;

        for (BottleResponse bottle : apiBottles) {
            WineDto wine = new WineDto(
                    bottle.getId(),
                    bottle.getTitle(),
                    bottle.getDescription(),
                    bottle.getVariety(),
                    bottle.getColor()
            );

            list.add(wine);
        }
        return list;
    }

    /**
     * Display the list of suggestions in the RecyclerView.
     * @param suggestions The list of suggestions to display.
     */
    private void displaySuggestions(List<WineDto> suggestions) {
        if (suggestions == null || suggestions.isEmpty()) {
            rvSuggestions.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvSuggestions.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            adapter.submitList(suggestions);
            resultViewModel.setWineList(suggestions);
        }
    }

    /**
     * Trigger a short vibration to indicate successful loading of suggestions.
     */
    private void triggerSuccessVibration() {
        Vibrator vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(50);
            }
        }
    }

    @Override
    /**
     * Handle the click event on a suggestion.
     * @param wine The selected wine.
     */
    public void onSuggestionClick(WineDto wine) {
        resultViewModel.setCurrentWine(wine);
        navController.navigate(R.id.action_suggestion_to_detail);
    }
}