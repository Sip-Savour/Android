package com.sipandsavour.ui.result;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.sipandsavour.R;
import com.sipandsavour.data.dto.WineDto;

public class SuggestionAdapter extends ListAdapter<WineDto, SuggestionAdapter.SuggestionViewHolder> {

    private OnSuggestionClickListener listener;

    public interface OnSuggestionClickListener {
        void onSuggestionClick(WineDto wine);
    }

    public SuggestionAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnSuggestionClickListener(OnSuggestionClickListener listener) {
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<WineDto> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull WineDto oldItem, @NonNull WineDto newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull WineDto oldItem, @NonNull WineDto newItem) {
                    return oldItem.getId() == newItem.getId()
                            && String.valueOf(oldItem.getTitle()).equals(String.valueOf(newItem.getTitle()));
                }
            };

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_suggestion_wine, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        WineDto wine = getItem(position);
        holder.bind(wine);
    }

    class SuggestionViewHolder extends RecyclerView.ViewHolder {

        // --- NOUVEAU : Ajout de la variable pour le titre ---
        private final TextView tvTitle;
        private final TextView tvCepage;
        private final TextView tvDescription;
        private final TextView tvType;

        SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            // --- NOUVEAU : Liaison avec l'ID du XML ---
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCepage = itemView.findViewById(R.id.tvCepage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvType = itemView.findViewById(R.id.tvType);
        }

        void bind(WineDto wine) {
            // --- NOUVEAU : Affichage du titre du vin ---
            if (tvTitle != null) {
                tvTitle.setText(wine.getTitle() != null ? wine.getTitle() : "Vin Inconnu");
            }

            tvCepage.setText(wine.getVariety() != null ? wine.getVariety() : "-");
            tvDescription.setText(wine.getDescription() != null ? wine.getDescription() : "-");
            tvType.setText(wine.getColorDisplayName());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSuggestionClick(wine);
                }
            });
        }
    }
}