package com.example.argapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argapp.Classes.OrderBill;
import com.example.argapp.R;

import java.util.List;

public class OrderBillAdapter  extends RecyclerView.Adapter<OrderBillAdapter.OrderBillViewHolder> {

    private List<OrderBill> orderBillList;
    private Context context;

    public OrderBillAdapter(List<OrderBill> orderBillList, Context context) {
        this.orderBillList = orderBillList;
        this.context = context;
    }

    public static class OrderBillViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvProductName, tvVariant, tvQuantity, tvPrice, tvAdditionalInfo;
        ImageView ivProductImage;
        Button btnStatus;

        public OrderBillViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvVariant = itemView.findViewById(R.id.tvVariant);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvAdditionalInfo = itemView.findViewById(R.id.tvAdditionalInfo);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            btnStatus = itemView.findViewById(R.id.btnStatus);
        }
    }


    @NonNull
    @Override
    public OrderBillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_order_history_item, parent, false);
        return new OrderBillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderBillViewHolder holder, int position) {

        OrderBill orderBill = orderBillList.get(position);
        if (orderBill == null)
            return;

        holder.tvOrderId.setText("7504375dvds");
//        holder.tvProductName.setText(bill.getProductName());
//        holder.tvVariant.setText(bill.getVariant());
//        holder.tvQuantity.setText(bill.getQuantity() + "x");
//        holder.tvPrice.setText("$" + bill.getPrice());
//        holder.btnStatus.setText(bill.getStatus());
//        holder.tvAdditionalInfo.setText("Order Received by " + bill.getAdditionalInfo());

        // Load image if needed (using Glide or Picasso)
//        Glide.with(context)
//                .load(bill.getImageUrl())
//                .placeholder(R.drawable.apple)
//                .into(holder.ivProductImage);

    }


    @Override
    public int getItemCount() {
        return this.orderBillList.size();
    }
}
