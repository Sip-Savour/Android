package com.sipandsavour.ui.selection;

import android.content.Context;
import android.graphics.Color;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MealCardAdapter extends RecyclerView.Adapter<MealCardAdapter.CardViewHolder> {

    private final List<String> items;
    private final boolean isSingleSelection;
    private final Set<String> selectedItems = new HashSet<>();
    private final OnSelectionChangeListener listener;

    /** Interface for listening to selection changes in the adapter.
     */
    public interface OnSelectionChangeListener {
        void onSelectionChanged(Set<String> selectedItems);
    }

    /** Constructor for the MealCardAdapter.
     * @param items List of items to display in the adapter.
     * @param isSingleSelection If true, only one item can be selected at a time. If false, multiple items can be selected.
     * @param listener Listener for selection changes. Can be null if no listener is needed.
     */
    public MealCardAdapter(List<String> items, boolean isSingleSelection, OnSelectionChangeListener listener) {
        this.items = items;
        this.isSingleSelection = isSingleSelection;
        this.listener = listener;
    }

    /** Get the set of selected items.
     * @return The set of selected items.
     */
    public Set<String> getSelectedItems() {
        return selectedItems;
    }

    @NonNull
    @Override
    /** Create a new view holder for the adapter.
     * @param parent The parent view group.
     * @param viewType The view type.
     * @return The created view holder.
     */
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    /** Bind the view holder with the data for the given position.
     * @param holder The view holder to bind.
     * @param position The position of the item to bind.
     */
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        String item = items.get(position);
        boolean isSelected = selectedItems.contains(item);
        holder.bind(item, isSelected);
    }

    @Override
    /** Get the number of items in the adapter.
     * @return The number of items in the adapter.
     */
    public int getItemCount() {
        return items.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardItem;
        private final ImageView ivMealIcon;
        private final TextView tvOptionName;

        /** Constructor for the CardViewHolder.
         * @param itemView The view for the card item.
         */
        CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardItem = itemView.findViewById(R.id.cardItem);
            ivMealIcon = itemView.findViewById(R.id.ivMealIcon);
            tvOptionName = itemView.findViewById(R.id.tvOptionName);
        }

        /** Bind the view holder with the data for the given position.
         * @param item The item to bind.
         * @param isSelected Whether the item is selected.
         */
        void bind(String item, boolean isSelected) {
            tvOptionName.setText(item);
            Context context = itemView.getContext();

            int iconRes = getIconForBase(item);
            if (iconRes != 0) {
                ivMealIcon.setVisibility(View.VISIBLE);
                ivMealIcon.setImageResource(iconRes);
            } else {
                ivMealIcon.setVisibility(View.GONE);
            }

            if (isSelected) {
                cardItem.setStrokeColor(ContextCompat.getColor(context, R.color.primary));
                cardItem.setCardBackgroundColor(ContextCompat.getColor(context, R.color.surface_variant));
                tvOptionName.setTextColor(ContextCompat.getColor(context, R.color.primary));
                if (iconRes != 0) {
                    ivMealIcon.setColorFilter(ContextCompat.getColor(context, R.color.primary));
                }
            } else {
                cardItem.setStrokeColor(Color.TRANSPARENT);
                cardItem.setCardBackgroundColor(ContextCompat.getColor(context, R.color.surface));

                // On utilise Color.GRAY pour le texte et l'icône non sélectionnés
                tvOptionName.setTextColor(Color.GRAY);
                if (iconRes != 0) {
                    ivMealIcon.setColorFilter(Color.GRAY);
                }
            }

            cardItem.setOnClickListener(v -> {
                HapticUtil.playConfirm(v);
                if (isSingleSelection) {
                    // Si on clique sur l'élément déjà sélectionné, on le désélectionne (ça permet de retirer la couleur)
                    if (selectedItems.contains(item)) {
                        selectedItems.clear();
                    } else {
                        selectedItems.clear();
                        selectedItems.add(item);
                    }
                    notifyDataSetChanged();
                } else {
                    if (selectedItems.contains(item)) {
                        selectedItems.remove(item);
                    } else {
                        selectedItems.add(item);
                    }
                    notifyItemChanged(getAdapterPosition());
                }

                if (listener != null) {
                    listener.onSelectionChanged(selectedItems);
                }
            });
        }

        /** Get the icon resource for the given base name.
         * @param baseName The name of the base.
         * @return The icon resource for the base, or 0 if not found.
         */
        private int getIconForBase(String baseName) {
            switch (baseName) {
                //case "Viande Rouge": return R.drawable.ic_meal_red_meat;
                //case "Viande Blanche": return R.drawable.ic_meal_white_meat;
                //case "Volaille": return R.drawable.ic_meal_poultry;
                //case "Poisson": return R.drawable.ic_meal_fish;
                //case "Fruits de mer": return R.drawable.ic_meal_seafood;
                //case "Végétarien": return R.drawable.ic_meal_veggie;
                //case "Fromage": return R.drawable.ic_meal_cheese;

                // Si vous avez des icônes de verres de vin :
                case "Vin Rouge": return R.drawable.ic_wine_red;
                case "Vin Blanc": return R.drawable.ic_wine_white;
                case "Vin Rosé": return R.drawable.ic_wine_rose;

                default: return 0;
            }
        }
    }
}