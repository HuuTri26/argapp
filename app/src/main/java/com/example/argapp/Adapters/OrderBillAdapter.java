package com.example.argapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.argapp.Classes.OrderBill;
import com.example.argapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderBillAdapter extends RecyclerView.Adapter<OrderBillAdapter.OrderBillViewHolder> {

    private List<OrderBill> orderBillList;
    private Context context;
    private OnOrderBillClickListener listener;

    // Interface để xử lý sự kiện click vào đơn hàng và hủy đơn hàng
    public interface OnOrderBillClickListener {
        void onOrderBillClick(OrderBill orderBill);

        void onCancelOrderClick(OrderBill orderBill, int position);
    }

    public OrderBillAdapter(List<OrderBill> orderBillList, Context context, OnOrderBillClickListener listener) {
        this.orderBillList = orderBillList;
        this.context = context;
        this.listener = listener;
    }

    public static class OrderBillViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvPrice, tvStatus;
        Button btnCancelOrder;

        public OrderBillViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnCancelOrder = itemView.findViewById(R.id.btnCancelOrder);
        }
    }

    @NonNull
    @Override
    public OrderBillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_bill, parent, false);
        return new OrderBillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderBillViewHolder holder, int position) {
        OrderBill orderBill = orderBillList.get(position);
        if (orderBill == null)
            return;

        // Hiển thị ID đơn hàng
        holder.tvOrderId.setText("Đơn hàng #" + orderBill.getOrderBillId());

        // Hiển thị ngày đặt hàng
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvOrderDate.setText(sdf.format(new Date(orderBill.getOrderDate())));

        // Hiển thị giá
        holder.tvPrice.setText(String.format("%.2f VNĐ", orderBill.getTotalPrice()));

        // Hiển thị trạng thái và xử lý màu sắc
        String status = orderBill.getStatus();
        holder.tvStatus.setText(status);

        // Thiết lập màu nền cho trạng thái
        if ("PENDING".equals(status)) {
            holder.tvStatus.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));
            holder.btnCancelOrder.setVisibility(View.VISIBLE);
        } else if ("PAID".equals(status) || "PAId".equals(status)) {
            holder.tvStatus.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
            holder.btnCancelOrder.setVisibility(View.GONE);
        } else if ("CANCELLED".equals(status)) {
            holder.tvStatus.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_dark));
            holder.btnCancelOrder.setVisibility(View.GONE);
        } else {
            holder.tvStatus.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
            holder.btnCancelOrder.setVisibility(View.GONE);
        }

        // Thiết lập sự kiện click cho item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderBillClick(orderBill);
            }
        });

        // Thiết lập sự kiện click cho nút hủy đơn hàng
        holder.btnCancelOrder.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancelOrderClick(orderBill, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.orderBillList != null ? this.orderBillList.size() : 0;
    }
    
    public void updateOrderList(List<OrderBill> newList) {
        this.orderBillList = newList;
        notifyDataSetChanged();
    }

    // Phương thức để cập nhật trạng thái của một đơn hàng cụ thể
    public void updateOrderStatus(int position, String newStatus) {
        if (position >= 0 && position < orderBillList.size()) {
            orderBillList.get(position).setStatus(newStatus);
            notifyItemChanged(position);
        }
    }
}