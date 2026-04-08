package com.sipandsavour.ui.selection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sipandsavour.R;
import com.sipandsavour.logic.FlavorMapper;
import com.sipandsavour.util.HapticUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Adapter pour l'accordéon des catégories de saveurs.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    /** Listener for flavor selection events. */
    public interface OnFlavorSelectionListener {
        void onFlavorToggled(String flavorKey);
        boolean isFlavorSelected(String flavorKey);
    }

    /** Listener for category click events. */
    public interface OnCategoryClickListener {
        void onCategoryToggled(int position);
    }

    /** The list of categories. */
    private List<FlavorMapper.AccordionCategory> categories = new ArrayList<>();
    private OnFlavorSelectionListener flavorListener;
    private OnCategoryClickListener categoryListener;

    /** Set the list of categories.
     * @param categories The list of categories.
     */
    public void setCategories(List<FlavorMapper.AccordionCategory> categories) {
        this.categories = categories != null ? categories : new ArrayList<>();
        notifyDataSetChanged();
    }

    /** Set the listener for flavor selection events.
     * @param listener The listener for flavor selection events.
     */
    public void setOnFlavorSelectionListener(OnFlavorSelectionListener listener) {
        this.flavorListener = listener;
    }

    /** Set the listener for category click events.
     * @param listener The listener for category click events.
     */
    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.categoryListener = listener;
    }

    /** Update the list of selected flavors.
     * @param selectedFlavors The set of selected flavor keys.
     */
    public void updateSelectedFlavors(Set<String> selectedFlavors) {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    /** Create a new view holder for the category item.
     * @param parent The parent view group.
     * @param viewType The view type.
     * @return The view holder for the category item.
     */
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_accordion_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    /** Bind the category data to the view holder.
     * @param holder The view holder for the category item.
     * @param position The position of the category in the list.
     */
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        FlavorMapper.AccordionCategory category = categories.get(position);
        holder.bind(category, position);
    }

    @Override
    /** Get the number of categories.
     * @return The number of categories.
     */
    public int getItemCount() {
        return categories.size();
    }

    // =======================================================
    //  VIEW HOLDER
    // =======================================================

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout layoutHeader;
        private final TextView tvTitle;
        private final ImageView ivExpand;
        private final ChipGroup chipGroup;

        /** Constructor for the category view holder.
         * @param itemView The view for the category item.
         */
        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutHeader = itemView.findViewById(R.id.layoutCategoryHeader);
            tvTitle = itemView.findViewById(R.id.tvCategoryTitle);
            ivExpand = itemView.findViewById(R.id.ivExpandIcon);
            chipGroup = itemView.findViewById(R.id.chipGroupFlavors);
        }

        /** Bind the category data to the view holder.
         * @param category The category data.
         * @param position The position of the category in the list.
         */
        void bind(FlavorMapper.AccordionCategory category, int position) {
            tvTitle.setText(category.getTitle());

            boolean isExpanded = category.isExpanded();
            chipGroup.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            ivExpand.setRotation(isExpanded ? 180f : 0f);

            layoutHeader.setOnClickListener(v -> {
                HapticUtil.playConfirm(v);
                if (categoryListener != null) {
                    categoryListener.onCategoryToggled(position);
                }
                animateExpand(!category.isExpanded());
            });

            createChips(category);
        }

        /** Create chips for the category.
         * @param category The category data.
         */
        private void createChips(FlavorMapper.AccordionCategory category) {
            chipGroup.removeAllViews();

            for (String subGroupKey : category.getSubGroupKeys()) {
                Chip chip = new Chip(itemView.getContext());
                chip.setText(FlavorMapper.getGroupDisplayName(subGroupKey));
                chip.setTag(subGroupKey);
                chip.setCheckable(true);
                chip.setClickable(true);

                chip.setChipBackgroundColorResource(R.color.chip_background_selector);
                chip.setTextColor(itemView.getContext().getResources()
                        .getColorStateList(R.color.chip_text_selector, null));
                chip.setChipStrokeColorResource(R.color.primary);
                chip.setChipStrokeWidth(1f);

                if (flavorListener != null) {
                    chip.setChecked(flavorListener.isFlavorSelected(subGroupKey));
                }

                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (flavorListener != null) {
                        flavorListener.onFlavorToggled(subGroupKey);
                    }
                });

                chipGroup.addView(chip);
            }
        }

        /** Update the state of all chips in the group.
         */
        private void updateChipsState() {
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                View child = chipGroup.getChildAt(i);
                if (child instanceof Chip) {
                    Chip chip = (Chip) child;
                    String key = (String) chip.getTag();
                    if (flavorListener != null && key != null) {
                        chip.setChecked(flavorListener.isFlavorSelected(key));
                    }
                }
            }
        }

        /** Animate the expansion of the category.
         * @param expand Whether to expand the category.
         */
        private void animateExpand(boolean expand) {
            float fromRotation = expand ? 0f : 180f;
            float toRotation = expand ? 180f : 0f;

            RotateAnimation rotate = new RotateAnimation(
                    fromRotation, toRotation,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f
            );
            rotate.setDuration(200);
            rotate.setFillAfter(true);
            ivExpand.startAnimation(rotate);
        }
    }
}