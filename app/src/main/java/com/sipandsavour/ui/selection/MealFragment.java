package com.sipandsavour.ui.selection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.sipandsavour.R;
import com.sipandsavour.util.HapticUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MealFragment extends Fragment {

    private NavController navController;
    private SelectionViewModel viewModel;

    private RecyclerView rvBaseOptions;
    private RecyclerView rvTasteOptions;
    private RecyclerView rvColorOptions;
    private MaterialButton btnFindWine;

    private MealCardAdapter baseAdapter;
    private MealCardAdapter tasteAdapter;
    private MealCardAdapter colorAdapter;

    // Dictionnaire pour faire correspondre le nom traduit (UI) avec la clé technique (API)
    private final Map<String, String> displayToApiMap = new HashMap<>();

    /**
     * Classe interne pour représenter une option de repas avec son nom affiché et sa valeur API
     */
    private static class MealOption {
        String displayName;
        String apiValue;

        /**
         * Constructeur de la classe MealOption.
         * @param displayName Le nom à afficher pour l'option.
         * @param apiValue La valeur technique pour l'option.
         */
        MealOption(String displayName, String apiValue) {
            this.displayName = displayName;
            this.apiValue = apiValue;
        }
    }

    /**
     * Retourne la liste des options de base pour les repas.
     * @return La liste des noms à afficher pour les options de base.
     */
    private List<String> getBaseOptions() {
        List<MealOption> options = Arrays.asList(
                new MealOption(getString(R.string.meal_base_red_meat), "Viande Rouge"),
                new MealOption(getString(R.string.meal_base_white_meat), "Viande Blanche"),
                new MealOption(getString(R.string.meal_base_poultry), "Volaille"),
                new MealOption(getString(R.string.meal_base_fish), "Poisson"),
                new MealOption(getString(R.string.meal_base_seafood), "Fruits de mer"),
                new MealOption(getString(R.string.meal_base_vegetarian), "Végétarien"),
                new MealOption(getString(R.string.meal_base_cheese), "Fromage")
        );
        return extractDisplayNames(options);
    }

    /**
     * Retourne la liste des options de saveur pour les repas.
     * @return La liste des noms à afficher pour les options de saveur.
     */
    private List<String> getTasteOptions() {
        List<MealOption> options = Arrays.asList(
                new MealOption(getString(R.string.meal_taste_fatty), "Gras"),
                new MealOption(getString(R.string.meal_taste_rich), "Riche"),
                new MealOption(getString(R.string.meal_taste_dry), "Sec"),
                new MealOption(getString(R.string.meal_taste_salty), "Salé"),
                new MealOption(getString(R.string.meal_taste_sweet), "Sucré"),
                new MealOption(getString(R.string.meal_taste_peppery), "Poivré"),
                new MealOption(getString(R.string.meal_taste_spicy), "Épicé"),
                new MealOption(getString(R.string.meal_taste_acidic), "Acide")
        );
        return extractDisplayNames(options);
    }

    /**
     * Retourne la liste des options de couleur pour les repas.
     * @return La liste des noms à afficher pour les options de couleur.
     */

    private List<String> getColorOptions() {
        List<MealOption> options = Arrays.asList(
                new MealOption(getString(R.string.meal_color_red), "Vin Rouge"),
                new MealOption(getString(R.string.meal_color_white), "Vin Blanc"),
                new MealOption(getString(R.string.meal_color_rose), "Vin Rosé")
        );
        return extractDisplayNames(options);
    }

    /**
     * Remplit le dictionnaire de traduction et retourne la liste des noms à afficher.
     * @param options La liste des options de repas.
     * @return La liste des noms à afficher.
     */
    private List<String> extractDisplayNames(List<MealOption> options) {
        List<String> displayNames = new ArrayList<>();
        for (MealOption opt : options) {
            displayNames.add(opt.displayName);
            displayToApiMap.put(opt.displayName, opt.apiValue);
        }
        return displayNames;
    }

    @Nullable
    @Override
    /** Called to create the view for the fragment.
     * @param inflater The layout inflater.
     * @param container The parent view group.
     * @param savedInstanceState The saved instance state.
     * @return The created view.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meal, container, false);
    }

    @Override
    /** Called after the view for the fragment has been created.
     * @param view The view for the fragment.
     * @param savedInstanceState The saved instance state.
     */
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);
        viewModel = new ViewModelProvider(requireActivity()).get(SelectionViewModel.class);

        rvBaseOptions = view.findViewById(R.id.rvBaseOptions);
        rvTasteOptions = view.findViewById(R.id.rvTasteOptions);
        rvColorOptions = view.findViewById(R.id.rvColorOptions);
        btnFindWine = view.findViewById(R.id.btnFindWine);

        setupRecyclerViews();
        setupButton();
    }

    /**
     * Configure les RecyclerViews pour afficher les options de repas.
     */
    private void setupRecyclerViews() {
        baseAdapter = new MealCardAdapter(getBaseOptions(), true, selectedItems -> {
            checkIfCanFindWine();
        });
        rvBaseOptions.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvBaseOptions.setAdapter(baseAdapter);

        tasteAdapter = new MealCardAdapter(getTasteOptions(), false, selectedItems -> {
            checkIfCanFindWine();
        });
        rvTasteOptions.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        rvTasteOptions.setAdapter(tasteAdapter);

        colorAdapter = new MealCardAdapter(getColorOptions(), true, selectedItems -> {
            checkIfCanFindWine();
        });
        rvColorOptions.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        rvColorOptions.setAdapter(colorAdapter);
    }

    /**
     * Vérifie si les options de base sont sélectionnées et active/désactive le bouton de recherche.
     */
    private void checkIfCanFindWine() {
        boolean hasBase = !baseAdapter.getSelectedItems().isEmpty();
        btnFindWine.setEnabled(hasBase);
    }

    /**
     * Configure le bouton de recherche.
     */
    private void setupButton() {
        btnFindWine.setOnClickListener(v -> {
            HapticUtil.playConfirm(v);

            // On prépare le Set avec les CLÉS TECHNIQUES pour l'API
            Set<String> allChoicesApiKeys = new HashSet<>();

            // On convertit chaque sélection visuelle en clé technique
            for (String displayItem : baseAdapter.getSelectedItems()) {
                allChoicesApiKeys.add(displayToApiMap.get(displayItem));
            }
            for (String displayItem : tasteAdapter.getSelectedItems()) {
                allChoicesApiKeys.add(displayToApiMap.get(displayItem));
            }
            for (String displayItem : colorAdapter.getSelectedItems()) {
                allChoicesApiKeys.add(displayToApiMap.get(displayItem));
            }

            viewModel.predictFromMeal(allChoicesApiKeys);
            navController.navigate(R.id.action_meal_to_suggestions);
        });
    }
}