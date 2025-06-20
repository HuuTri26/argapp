package com.example.argapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argapp.Classes.Coupon;
import com.example.argapp.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdminCouponsAdapter extends RecyclerView.Adapter<AdminCouponsAdapter.ViewHolder> {

    private List<Coupon> couponsList;
    private OnCouponActionListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public interface OnCouponActionListener {
        void onEditCoupon(Coupon coupon);
        void onDeleteCoupon(Coupon coupon);
    }

    public AdminCouponsAdapter(List<Coupon> couponsList, OnCouponActionListener listener) {
        this.couponsList = couponsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_coupon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Coupon coupon = couponsList.get(position);

        holder.tvCouponId.setText(coupon.getId());
        holder.tvCouponType.setText(coupon.getType());

        if (coupon.getType() != null && coupon.getType().equals("percentage")) {
            holder.tvCouponValue.setText(String.format("%.1f%%", coupon.getDiscountValue()));
        } else {
            holder.tvCouponValue.setText(String.format("%.0f VND", coupon.getDiscountValue()));
        }

        holder.tvCouponProduct.setText(coupon.getProductId() != null && !coupon.getProductId().isEmpty()
                ? "Sản phẩm: " + coupon.getProductId() : "Áp dụng cho tất cả");

        String dateRange = "";
        if (coupon.getStartDate() != null && coupon.getEndDate() != null) {
            dateRange = dateFormat.format(coupon.getStartDate()) + " - " + dateFormat.format(coupon.getEndDate());
        }
        holder.tvCouponDateRange.setText(dateRange);

        holder.tvCouponDescription.setText(coupon.getDescription() != null ? coupon.getDescription() : "");

        holder.btnEdit.setOnClickListener(v -> listener.onEditCoupon(coupon));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteCoupon(coupon));
    }

    @Override
    public int getItemCount() {
        return couponsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCouponId, tvCouponType, tvCouponValue, tvCouponProduct, tvCouponDateRange, tvCouponDescription;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCouponId = itemView.findViewById(R.id.tv_coupon_id);
            tvCouponType = itemView.findViewById(R.id.tv_coupon_type);
            tvCouponValue = itemView.findViewById(R.id.tv_coupon_value);
            tvCouponProduct = itemView.findViewById(R.id.tv_coupon_product);
            tvCouponDateRange = itemView.findViewById(R.id.tv_coupon_date_range);
            tvCouponDescription = itemView.findViewById(R.id.tv_coupon_description);
            btnEdit = itemView.findViewById(R.id.btn_edit_coupon);
            btnDelete = itemView.findViewById(R.id.btn_delete_coupon);
        }
    }
}