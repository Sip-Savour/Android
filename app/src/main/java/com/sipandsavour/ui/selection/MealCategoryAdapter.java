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

    /** Interface for listening to category selection events.
     */
    public interface OnCategoryClickListener {
        void onCategorySelected(String categoryKey);
    }

    /** Constructor for the MealCategoryAdapter.
     * @param categories The list of meal categories.
     * @param listener The listener for category selection events.
     */
    public MealCategoryAdapter(List<MealCategory> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    /** Create a new view holder for the adapter.
     * @param parent The parent view group.
     * @param viewType The view type.
     * @return The created view holder.
     */
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    /** Bind the view holder with the data for the given position.
     * @param holder The view holder to bind.
     * @param position The position of the item to bind.
     */
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        MealCategory category = categories.get(position);
        holder.bind(category, selectedKey != null && selectedKey.equals(category.key));
    }

    @Override
    /** Get the number of items in the adapter.
     * @return The number of items in the adapter.
     */
    public int getItemCount() {
        return categories.size();
    }

    /** Set the selected category.
     * @param key The key of the selected category.
     */
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

        /** Constructor for the CategoryViewHolder.
         * @param itemView The view for the category item.
         */
        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardCategory);
            ivIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvName = itemView.findViewById(R.id.tvCategoryName);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);
        }

        /** Bind the view holder with the data for the given position.
         * @param category The meal category to bind.
         * @param isSelected Whether the category is selected.
         */
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

    /** Represents a meal category.
     */
    public static class MealCategory {
        public final String key;
        public final String name;
        public final int iconRes;
        public final int colorRes;

        /** Constructor for the MealCategory.
         * @param key The unique key for the category.
         * @param name The display name of the category.
         * @param iconRes The resource ID for the category icon.
         * @param colorRes The resource ID for the category color.
         */
        public MealCategory(String key, String name, int iconRes, int colorRes) {
            this.key = key;
            this.name = name;
            this.iconRes = iconRes;
            this.colorRes = colorRes;
        }
    }
}