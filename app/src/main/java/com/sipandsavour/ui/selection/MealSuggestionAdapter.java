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

    public interface OnMealClickListener {
        void onMealClicked(MealDto meal);
    }

    public void setOnMealClickListener(OnMealClickListener listener) {
        this.onMealClickListener = listener;
    }

    public void setMeals(List<MealDto> meals) {
        this.meals = meals != null ? meals : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealSuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal_suggestion, parent, false);
        return new MealSuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealSuggestionViewHolder holder, int position) {
        MealDto meal = meals.get(position);
        holder.bind(meal, onMealClickListener);
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public static class MealSuggestionViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView tvMealName;

        public MealSuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.mealSuggestionCard);
            tvMealName = itemView.findViewById(R.id.tvMealName);
        }

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

