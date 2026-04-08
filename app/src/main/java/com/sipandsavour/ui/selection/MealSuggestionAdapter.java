package com.sipandsavour.ui.selection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.sipandsavour.R;
import com.sipandsavour.data.dto.meal.MealDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter pour afficher les suggestions de repas dans une recherche avancée
 */
public class MealSuggestionAdapter extends RecyclerView.Adapter<MealSuggestionAdapter.MealSuggestionViewHolder> {

    private List<MealDto> meals = new ArrayList<>();
    private OnMealClickListener onMealClickListener;

    /**
     * Interface pour gérer les clics sur les suggestions de repas.
     */
    public interface OnMealClickListener {
        void onMealClicked(MealDto meal);
    }

    /**
     * Définit le listener pour les clics sur les suggestions de repas.
     * @param listener Le listener à définir.
     */
    public void setOnMealClickListener(OnMealClickListener listener) {
        this.onMealClickListener = listener;
    }

    /**
     * Définit la liste des repas à afficher.
     * @param meals La liste des repas à afficher.
     */
    public void setMeals(List<MealDto> meals) {
        this.meals = meals != null ? meals : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    /** Called to create the view holder for the recycler view.
     * @param parent The parent view group.
     * @param viewType The view type.
     * @return The created view holder.
     */
    public MealSuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal_suggestion, parent, false);
        return new MealSuggestionViewHolder(view);
    }

    @Override
    /** Called to bind the view holder with the data.
     * @param holder The view holder to bind.
     * @param position The position of the data.
     */
    public void onBindViewHolder(@NonNull MealSuggestionViewHolder holder, int position) {
        MealDto meal = meals.get(position);
        holder.bind(meal, onMealClickListener);
    }

    @Override
    /** Returns the number of items in the list.
     * @return The number of items.
     */
    public int getItemCount() {
        return meals.size();
    }

    /**
     * ViewHolder pour les suggestions de repas.
     */
    public static class MealSuggestionViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView tvMealName;

        /**
         * Constructeur de la classe MealSuggestionViewHolder.
         * @param itemView La vue de l'élément.
         */
        public MealSuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.mealSuggestionCard);
            tvMealName = itemView.findViewById(R.id.tvMealName);
        }

        /**
         * Lie les données du repas à la vue.
         * @param meal Le repas à afficher.
         * @param listener Le listener pour les clics.
         */
        public void bind(MealDto meal, OnMealClickListener listener) {
            tvMealName.setText(meal.getStrMeal());
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMealClicked(meal);
                }
            });
        }
    }
}

