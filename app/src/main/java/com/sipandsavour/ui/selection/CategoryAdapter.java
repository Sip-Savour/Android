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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Adapter pour l'accordéon des catégories de saveurs.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnFlavorSelectionListener {
        void onFlavorToggled(String flavorKey);
        boolean isFlavorSelected(String flavorKey);
    }

    public interface OnCategoryClickListener {
        void onCategoryToggled(int position);
    }

    private List<FlavorMapper.AccordionCategory> categories = new ArrayList<>();
    private OnFlavorSelectionListener flavorListener;
    private OnCategoryClickListener categoryListener;

    public void setCategories(List<FlavorMapper.AccordionCategory> categories) {
        this.categories = categories != null ? categories : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnFlavorSelectionListener(OnFlavorSelectionListener listener) {
        this.flavorListener = listener;
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.categoryListener = listener;
    }

    public void updateSelectedFlavors(Set<String> selectedFlavors) {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_accordion_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        FlavorMapper.AccordionCategory category = categories.get(position);
        holder.bind(category, position);
    }

    @Override
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

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutHeader = itemView.findViewById(R.id.layoutCategoryHeader);
            tvTitle = itemView.findViewById(R.id.tvCategoryTitle);
            ivExpand = itemView.findViewById(R.id.ivExpandIcon);
            chipGroup = itemView.findViewById(R.id.chipGroupFlavors);
        }

        void bind(FlavorMapper.AccordionCategory category, int position) {
            tvTitle.setText(category.getTitle());

            boolean isExpanded = category.isExpanded();
            chipGroup.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            ivExpand.setRotation(isExpanded ? 180f : 0f);

            layoutHeader.setOnClickListener(v -> {
                if (categoryListener != null) {
                    categoryListener.onCategoryToggled(position);
                }
                animateExpand(!category.isExpanded());
            });

            createChips(category);
        }

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