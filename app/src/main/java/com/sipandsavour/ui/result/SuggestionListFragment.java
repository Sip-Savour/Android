package com.sipandsavour.ui.result;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sipandsavour.R;
import com.sipandsavour.data.dto.WineDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuggestionListFragment extends Fragment implements SuggestionAdapter.OnSuggestionClickListener {

    private NavController navController;
    private ResultViewModel resultViewModel;

    private RecyclerView rvSuggestions;
    private LinearLayout layoutEmpty;

    private SuggestionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suggestion_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);
        resultViewModel = new ViewModelProvider(requireActivity()).get(ResultViewModel.class);

        bindViews(view);
        setupRecyclerView();
        loadSuggestions();
    }

    private void bindViews(View view) {
        rvSuggestions = view.findViewById(R.id.rvSuggestions);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
    }

    private void setupRecyclerView() {
        adapter = new SuggestionAdapter();
        adapter.setOnSuggestionClickListener(this);
        rvSuggestions.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSuggestions.setAdapter(adapter);
    }

    private void loadSuggestions() {
        // Données de démonstration — 5 vins
        // Remplacer par l'appel API réel via Repository.predict()
        List<WineDto> suggestions = createDemoSuggestions();
        displaySuggestions(suggestions);
    }

    private void displaySuggestions(List<WineDto> suggestions) {
        if (suggestions == null || suggestions.isEmpty()) {
            rvSuggestions.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvSuggestions.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            adapter.submitList(suggestions);
        }
    }

    private List<WineDto> createDemoSuggestions() {
        List<WineDto> list = new ArrayList<>();

        WineDto wine1 = new WineDto(1, "Château Margaux 2018", "Cabernet Sauvignon",
                "Un grand cru classé aux arômes de cassis, de violette et de cèdre. " +
                "Tanins soyeux, finale longue et élégante.");
        wine1.setColor("red");
        wine1.setKeywords(Arrays.asList("fruité", "tannique", "élégant"));
        list.add(wine1);

        WineDto wine2 = new WineDto(2, "Penfolds Grange 2017", "Shiraz",
                "Vin australien puissant aux notes de fruits noirs, de chocolat " +
                "et d'épices. Structure imposante, grande complexité.");
        wine2.setColor("red");
        wine2.setKeywords(Arrays.asList("puissant", "épicé", "complexe"));
        list.add(wine2);

        WineDto wine3 = new WineDto(3, "Cloudy Bay 2022", "Sauvignon Blanc",
                "Vin néo-zélandais vif et aromatique. Notes de fruits de la passion, " +
                "de pamplemousse et d'herbes fraîches.");
        wine3.setColor("white");
        wine3.setKeywords(Arrays.asList("frais", "aromatique", "vif"));
        list.add(wine3);

        WineDto wine4 = new WineDto(4, "Whispering Angel 2023", "Grenache",
                "Rosé de Provence élégant aux arômes de fraise, de pêche blanche " +
                "et de fleurs. Frais et délicat.");
        wine4.setColor("rose");
        wine4.setKeywords(Arrays.asList("frais", "délicat", "fruité"));
        list.add(wine4);

        WineDto wine5 = new WineDto(5, "Barolo Monfortino 2015", "Nebbiolo",
                "Grand vin piémontais aux arômes de rose, de goudron et de cerise. " +
                "Tanins fermes, potentiel de garde exceptionnel.");
        wine5.setColor("red");
        wine5.setKeywords(Arrays.asList("tannique", "complexe", "garde"));
        list.add(wine5);

        return list;
    }

    @Override
    public void onSuggestionClick(WineDto wine) {
        resultViewModel.setCurrentWine(wine);
        navController.navigate(R.id.action_suggestion_to_detail);
    }
}