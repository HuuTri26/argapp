package com.example.argapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argapp.Classes.Item;
import com.example.argapp.R;

import java.util.List;

public class AdminItemsAdapter extends RecyclerView.Adapter<AdminItemsAdapter.ViewHolder> {

    private List<Item> itemsList;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onEditItem(Item item);
        void onDeleteItem(Item item);
    }

    public AdminItemsAdapter(List<Item> itemsList, OnItemActionListener listener) {
        this.itemsList = itemsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemsList.get(position);

        holder.tvName.setText(item.getName());
        holder.tvType.setText(item.getType());
        holder.tvPrice.setText(String.format("%.2f VND", item.getPrice()));
        holder.tvDescription.setText(item.getDescription());
        holder.tvUnit.setText(item.getUnit());

        // Load image (you might want to use Glide or Picasso for better image loading)
        if (item.getImage() != null && !item.getImage().isEmpty()) {
            // Load image from drawable or URL
            String imageName = item.getImage().replace("drawable/", "");
            int resourceId = holder.itemView.getContext().getResources()
                    .getIdentifier(imageName, "drawable", holder.itemView.getContext().getPackageName());
            if (resourceId != 0) {
                holder.ivImage.setImageResource(resourceId);
            } else {
                holder.ivImage.setImageResource(R.drawable.apple); // default image
            }
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEditItem(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteItem(item));
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvType, tvPrice, tvDescription, tvUnit;
        ImageView ivImage;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvType = itemView.findViewById(R.id.tv_item_type);
            tvPrice = itemView.findViewById(R.id.tv_item_price);
            tvDescription = itemView.findViewById(R.id.tv_item_description);
            tvUnit = itemView.findViewById(R.id.tv_item_unit);
            ivImage = itemView.findViewById(R.id.iv_item_image);
            btnEdit = itemView.findViewById(R.id.btn_edit_item);
            btnDelete = itemView.findViewById(R.id.btn_delete_item);
        }
    }
}