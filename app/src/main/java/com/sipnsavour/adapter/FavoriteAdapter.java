package com.sipnsavour.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sipnsavour.app.R;
import com.sipnsavour.model.dto.Wine;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private Context context;
    private List<Wine> favoriteList;
    private OnFavoriteClickListener listener;

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Wine wine);
    }

    public FavoriteAdapter(Context context, List<Wine> favoriteList, OnFavoriteClickListener listener) {
        this.context = context;
        this.favoriteList = favoriteList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Wine wine = favoriteList.get(position);
        holder.btnWineName.setText("Vin");

        holder.btnWineName.setOnClickListener(v -> listener.onFavoriteClick(wine));
        holder.ivWineImage.setOnClickListener(v -> listener.onFavoriteClick(wine));
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView ivWineImage;
        Button btnWineName;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivWineImage = itemView.findViewById(R.id.iv_wine_image);
            btnWineName = itemView.findViewById(R.id.btn_wine_name);
        }
    }
}