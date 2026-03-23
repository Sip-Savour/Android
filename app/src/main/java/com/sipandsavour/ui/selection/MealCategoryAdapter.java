package com.sipandsavour.ui.selection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.sipandsavour.R;
import com.sipandsavour.util.HapticUtil;

import java.util.List;

public class MealCategoryAdapter extends RecyclerView.Adapter<MealCategoryAdapter.CategoryViewHolder> {

    private final List<MealCategory> categories;
    private final OnCategoryClickListener listener;
    private String selectedKey = null;

    public interface OnCategoryClickListener {
        void onCategorySelected(String categoryKey);
    }

    public MealCategoryAdapter(List<MealCategory> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        MealCategory category = categories.get(position);
        holder.bind(category, selectedKey != null && selectedKey.equals(category.key));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setSelectedCategory(String key) {
        String oldKey = selectedKey;
        selectedKey = key;

        for (int i = 0; i < categories.size(); i++) {
            String catKey = categories.get(i).key;
            if (catKey.equals(key) || catKey.equals(oldKey)) {
                notifyItemChanged(i);
            }
        }
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardView;
        private final ImageView ivIcon;
        private final TextView tvName;
        private final View colorIndicator;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardCategory);
            ivIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvName = itemView.findViewById(R.id.tvCategoryName);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);
        }

        void bind(MealCategory category, boolean isSelected) {
            ivIcon.setImageResource(category.iconRes);
            tvName.setText(category.name);
            colorIndicator.setBackgroundColor(
                    ContextCompat.getColor(itemView.getContext(), category.colorRes));

            if (isSelected) {
                cardView.setStrokeWidth(3);
                cardView.setStrokeColor(
                        ContextCompat.getColor(itemView.getContext(), R.color.primary));
                cardView.setCardElevation(
                        itemView.getResources().getDimension(R.dimen.elevation_high));
            } else {
                cardView.setStrokeWidth(0);
                cardView.setCardElevation(
                        itemView.getResources().getDimension(R.dimen.elevation_medium));
            }

            itemView.setOnClickListener(v -> {
                HapticUtil.playConfirm(v);
                if (listener != null) {
                    listener.onCategorySelected(category.key);
                }
            });
        }
    }

    public static class MealCategory {
        public final String key;
        public final String name;
        public final int iconRes;
        public final int colorRes;

        public MealCategory(String key, String name, int iconRes, int colorRes) {
            this.key = key;
            this.name = name;
            this.iconRes = iconRes;
            this.colorRes = colorRes;
        }
    }
}