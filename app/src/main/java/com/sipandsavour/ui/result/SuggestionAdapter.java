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
import com.sipandsavour.util.HapticUtil;

public class SuggestionAdapter extends ListAdapter<WineDto, SuggestionAdapter.SuggestionViewHolder> {

    private OnSuggestionClickListener listener;

    /**
     * Interface pour gérer les clics sur les suggestions de vins.
     */
    public interface OnSuggestionClickListener {
        void onSuggestionClick(WineDto wine);
    }
    /** Constructeur par défaut. */
    public SuggestionAdapter() {
        super(DIFF_CALLBACK);
    }

    /**
     * Définit le listener pour les clics sur les suggestions.
     * @param listener Le listener à définir.
     */
    public void setOnSuggestionClickListener(OnSuggestionClickListener listener) {
        this.listener = listener;
    }

    /**
     * Callback pour calculer les différences entre les éléments de la liste.
     */
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
    /**
     * Inflate le layout pour chaque élément de la liste et crée un ViewHolder.
     */
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_suggestion_wine, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    /**
     * Lie les données du vin à la vue du ViewHolder.
     */
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        WineDto wine = getItem(position);
        holder.bind(wine);
    }

    class SuggestionViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;
        private final TextView tvCepage;
        private final TextView tvDescription;
        private final TextView tvType;

        /**
         * Constructeur du ViewHolder.
         * @param itemView La vue de l'élément.
         */
        SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCepage = itemView.findViewById(R.id.tvCepage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvType = itemView.findViewById(R.id.tvType);
        }

        /**
         * Lie les données du vin aux vues correspondantes.
         * @param wine Le vin à afficher.
         */
        void bind(WineDto wine) {
            if (tvTitle != null) {
                tvTitle.setText(wine.getTitle() != null ? wine.getTitle() : itemView.getContext().getString(R.string.result_unknown_wine));
            }

            tvCepage.setText(wine.getVariety() != null ? wine.getVariety() : "-");
            tvDescription.setText(wine.getDescription() != null ? wine.getDescription() : "-");
            tvType.setText(wine.getColorDisplayName());

            itemView.setOnClickListener(v -> {
                HapticUtil.playConfirm(v);
                if (listener != null) {
                    listener.onSuggestionClick(wine);
                }
            });
        }
    }
}