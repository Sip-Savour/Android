package com.sipandsavour.ui.favorites;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.sipandsavour.R;
import com.sipandsavour.data.dto.WineDto;
import com.sipandsavour.util.HapticUtil;

/**
 * Adapter pour la liste des favoris.
 */
public class FavoritesAdapter extends ListAdapter<WineDto, FavoritesAdapter.FavoriteViewHolder> {

    public interface OnFavoriteClickListener {
        void onFavoriteClick(WineDto wine, int position);
    }

    private OnFavoriteClickListener listener;

    public FavoritesAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<WineDto> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<WineDto>() {
                @Override
                public boolean areItemsTheSame(@NonNull WineDto oldItem, @NonNull WineDto newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull WineDto oldItem, @NonNull WineDto newItem) {
                    return oldItem.getTitle() != null &&
                            oldItem.getTitle().equals(newItem.getTitle());
                }
            };

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wine, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        WineDto wine = getItem(position);
        holder.bind(wine, position);
    }

    // =======================================================
    //  VIEW HOLDER
    // =======================================================

    class FavoriteViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivWineImage;
        private final TextView tvWineName;

        FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivWineImage = itemView.findViewById(R.id.ivFavoriteWine);
            tvWineName = itemView.findViewById(R.id.tvFavoriteName);
        }

        void bind(WineDto wine, int position) {
            tvWineName.setText(wine.getTitle() != null ? wine.getTitle() : wine.getVariety());

            int imageRes = getWineImageRes(wine.getColor());
            ivWineImage.setImageResource(imageRes);

            itemView.setOnClickListener(v -> {
                HapticUtil.playConfirm(v);
                if (listener != null) {
                    listener.onFavoriteClick(wine, position);
                }
            });

            tvWineName.setOnClickListener(v -> {
                HapticUtil.playConfirm(v);
                if (listener != null) {
                    listener.onFavoriteClick(wine, position);
                }
            });
        }

        private int getWineImageRes(String color) {
            if (color == null) return R.drawable.ic_wine_red;

            switch (color.toLowerCase()) {
                case "white":
                    return R.drawable.ic_wine_white;
                case "rose":
                case "rosé":
                    return R.drawable.ic_wine_rose;
                default:
                    return R.drawable.ic_wine_red;
            }
        }
    }
}