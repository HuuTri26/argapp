package com.example.argapp.Adapters;

import android.graphics.Color;
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

public class AdminOrdersAdapter extends RecyclerView.Adapter<AdminOrdersAdapter.ViewHolder> {

    private List<OrderBill> ordersList;
    private OnOrderActionListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public interface OnOrderActionListener {
        void onUpdateOrderStatus(OrderBill order);
        void onViewOrderDetails(OrderBill order);
        void onDeleteOrder(OrderBill order);
    }

    public AdminOrdersAdapter(List<OrderBill> ordersList, OnOrderActionListener listener) {
        this.ordersList = ordersList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderBill order = ordersList.get(position);

        holder.tvOrderId.setText("Đơn hàng: " + (order.getOrderBillId() != null ? order.getOrderBillId() : "N/A"));
        holder.tvOrderUser.setText("Khách hàng: " + (order.getUserUid() != null ? order.getUserUid() : "N/A"));
        holder.tvOrderDate.setText("Ngày: " + dateFormat.format(new Date(order.getOrderDate())));
        holder.tvOrderTotal.setText(String.format("%.2f VND", order.getTotalPrice()));

        String status = order.getStatus() != null ? order.getStatus() : "pending";
        holder.tvOrderStatus.setText(getStatusText(status));
        holder.tvOrderStatus.setBackgroundColor(getStatusColor(status));

        holder.tvOrderAddress.setText("Địa chỉ: " + (order.getAddress() != null ? order.getAddress() : "Chưa có"));

        holder.btnUpdateStatus.setOnClickListener(v -> listener.onUpdateOrderStatus(order));
        holder.btnViewDetails.setOnClickListener(v -> listener.onViewOrderDetails(order));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteOrder(order));
    }

    private String getStatusText(String status) {
        switch (status) {
            case "pending": return "Chờ xử lý";
            case "confirmed": return "Đã xác nhận";
            case "processing": return "Đang xử lý";
            case "shipped": return "Đã gửi hàng";
            case "delivered": return "Đã giao hàng";
            case "cancelled": return "Đã hủy";
            default: return "Không xác định";
        }
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "pending": return Color.parseColor("#FF9800");
            case "confirmed": return Color.parseColor("#2196F3");
            case "processing": return Color.parseColor("#9C27B0");
            case "shipped": return Color.parseColor("#00BCD4");
            case "delivered": return Color.parseColor("#4CAF50");
            case "cancelled": return Color.parseColor("#F44336");
            default: return Color.parseColor("#757575");
        }
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderUser, tvOrderDate, tvOrderTotal, tvOrderStatus, tvOrderAddress;
        Button btnUpdateStatus, btnViewDetails, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderUser = itemView.findViewById(R.id.tv_order_user);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderTotal = itemView.findViewById(R.id.tv_order_total);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvOrderAddress = itemView.findViewById(R.id.tv_order_address);
            btnUpdateStatus = itemView.findViewById(R.id.btn_update_status);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
            btnDelete = itemView.findViewById(R.id.btn_delete_order);
        }
    }
}