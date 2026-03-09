package com.sipnsavour.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sipnsavour.app.R;
import com.sipnsavour.model.dto.Wine;

import java.util.List;

public class WineGridAdapter extends RecyclerView.Adapter<WineGridAdapter.WineViewHolder> {

    private final Context context;
    private final List<Wine> wineList;
    private final OnWineClickListener listener;
    private boolean isGridView = true;

    public interface OnWineClickListener {
        void onWineClick(Wine wine);
        void onFavoriteClick(Wine wine, int position);
    }

    public WineGridAdapter(Context context, List<Wine> wineList, OnWineClickListener listener) {
        this.context = context;
        this.wineList = wineList;
        this.listener = listener;
    }

    public void setGridView(boolean isGridView) {
        this.isGridView = isGridView;
    }

    @NonNull
    @Override
    public WineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wine_grid, parent, false);
        return new WineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WineViewHolder holder, int position) {
        Wine wine = wineList.get(position);

        holder.tvWineName.setText(wine.getCepage());
        holder.tvType.setText(wine.getType());
        holder.tvDescription.setText(wine.getDescription());

        int badgeColor = getWineTypeColor(wine.getType());
        holder.tvType.setBackgroundTintList(android.content.res.ColorStateList.valueOf(badgeColor));

        updateFavoriteIcon(holder.ivFavorite, wine.isFavorite());

        holder.cardView.setOnClickListener(v -> listener.onWineClick(wine));
        holder.ivFavorite.setOnClickListener(v -> listener.onFavoriteClick(wine, position));
    }

    @Override
    public int getItemCount() {
        return wineList.size();
    }

    private int getWineTypeColor(String type) {
        switch (type) {
            case "Rouge":
                return ContextCompat.getColor(context, R.color.wine_red);
            case "Blanc":
                return ContextCompat.getColor(context, R.color.wine_white);
            case "Rosé":
                return ContextCompat.getColor(context, R.color.wine_rose);
            default:
                return ContextCompat.getColor(context, R.color.burgundy);
        }
    }

    private void updateFavoriteIcon(ImageView ivFavorite, boolean isFavorite) {
        if (isFavorite) {
            ivFavorite.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            ivFavorite.setImageResource(R.drawable.ic_favorite_outline);
        }
        ivFavorite.setColorFilter(ContextCompat.getColor(context, R.color.burgundy));
    }

    public static class WineViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivWineImage;
        ImageView ivFavorite;
        TextView tvWineName;
        TextView tvType;
        TextView tvDescription;
        TextView tvRating;

        public WineViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cv_wine_item);
            ivWineImage = itemView.findViewById(R.id.iv_wine_image);
            ivFavorite = itemView.findViewById(R.id.iv_favorite);
            tvWineName = itemView.findViewById(R.id.tv_wine_name);
            tvType = itemView.findViewById(R.id.tv_type);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvRating = itemView.findViewById(R.id.tv_rating);
        }
    }
}