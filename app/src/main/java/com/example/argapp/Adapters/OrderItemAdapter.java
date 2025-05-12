package com.example.argapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argapp.Classes.OrderBillItem;
import com.example.argapp.R;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {
    private List<OrderBillItem> itemList;
    private Context context;

    public OrderItemAdapter(List<OrderBillItem> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItemImage;
        TextView tvItemName, tvItemPrice, tvItemQuantity;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.ivOrderItemImage);
            tvItemName = itemView.findViewById(R.id.tvOrderItemName);
            tvItemPrice = itemView.findViewById(R.id.tvOrderItemPrice);
            tvItemQuantity = itemView.findViewById(R.id.tvOrderItemQuantity);
        }
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_product, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderBillItem item = itemList.get(position);
        if (item == null) {
            return;
        }

        // Hiển thị tên sản phẩm
        holder.tvItemName.setText(item.getProductName());

        // Hiển thị giá sản phẩm
        holder.tvItemPrice.setText(String.format("%.2f VNĐ", item.getSalePrice()));

        // Hiển thị số lượng
        holder.tvItemQuantity.setText(String.format("Số lượng: %d %s", item.getQuantity(),
                item.getUnit() != null ? item.getUnit() : ""));

        // Hiển thị hình ảnh
        if (item.getImage() != null && !item.getImage().isEmpty()) {
            String imageName = item.getImage();
            if (imageName.contains("/")) {
                imageName = imageName.substring(imageName.lastIndexOf("/") + 1);
            }

            int resourceId = context.getResources().getIdentifier(
                    imageName, "drawable", context.getPackageName());

            if (resourceId != 0) {
                holder.ivItemImage.setImageResource(resourceId);
            } else {
                // Sử dụng hình mặc định
                holder.ivItemImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            // Sử dụng hình mặc định
            holder.ivItemImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }
}