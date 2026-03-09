package com.sipnsavour.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sipnsavour.app.R;
import com.sipnsavour.model.dto.Wine;

import java.util.List;

public class WineAdapter extends RecyclerView.Adapter<WineAdapter.WineViewHolder> {

    private Context context;
    private List<Wine> wineList;
    private OnWineClickListener listener;

    public interface OnWineClickListener {
        void onWineClick(Wine wine);
    }

    public WineAdapter(Context context, List<Wine> wineList, OnWineClickListener listener) {
        this.context = context;
        this.wineList = wineList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wine, parent, false);
        return new WineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WineViewHolder holder, int position) {
        Wine wine = wineList.get(position);
        holder.tvCepage.setText("Cépage: " + wine.getCepage());
        holder.tvDescription.setText("Description:\n" + wine.getDescription());
        holder.tvType.setText("Type: " + wine.getType());

        holder.cardView.setOnClickListener(v -> listener.onWineClick(wine));
    }

    @Override
    public int getItemCount() {
        return wineList.size();
    }

    static class WineViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvCepage, tvDescription, tvType;
        ImageView ivWineIcon;

        public WineViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cv_wine_item);
            tvCepage = itemView.findViewById(R.id.tv_cepage);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvType = itemView.findViewById(R.id.tv_type);
            ivWineIcon = itemView.findViewById(R.id.iv_wine_icon);
        }
    }
}
