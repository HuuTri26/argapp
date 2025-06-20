package com.example.argapp.Fragments.Admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argapp.Adapters.AdminOrdersAdapter;
import com.example.argapp.Classes.OrderBill;
import com.example.argapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminOrdersFragment extends Fragment implements AdminOrdersAdapter.OnOrderActionListener {

    private RecyclerView recyclerView;
    private AdminOrdersAdapter adapter;
    private List<OrderBill> ordersList;
    private DatabaseReference ordersRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_orders, container, false);

        initViews(view);
        setupRecyclerView();
        setupDatabase();
        loadOrders();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_orders);
    }

    private void setupRecyclerView() {
        ordersList = new ArrayList<>();
        adapter = new AdminOrdersAdapter(ordersList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupDatabase() {
        ordersRef = FirebaseDatabase.getInstance().getReference("Data/OrderBills");
    }

    private void loadOrders() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ordersList.clear();
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    OrderBill order = orderSnapshot.getValue(OrderBill.class);
                    if (order != null) {
                        ordersList.add(order);
                    }
                }
                // Sort by date (newest first)
                ordersList.sort((o1, o2) -> Long.compare(o2.getOrderDate(), o1.getOrderDate()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải đơn hàng: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUpdateOrderStatus(OrderBill order) {
        showUpdateStatusDialog(order);
    }

    @Override
    public void onViewOrderDetails(OrderBill order) {
        showOrderDetailsDialog(order);
    }

    @Override
    public void onDeleteOrder(OrderBill order) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa đơn hàng này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteOrder(order))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showUpdateStatusDialog(OrderBill order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update_order_status, null);
        builder.setView(dialogView);

        Spinner spinnerStatus = dialogView.findViewById(R.id.spinner_order_status);

        String[] statuses = {"pending", "confirmed", "processing", "shipped", "delivered", "cancelled"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, statuses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        // Set current status
        String currentStatus = order.getStatus() != null ? order.getStatus() : "pending";
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equals(currentStatus)) {
                spinnerStatus.setSelection(i);
                break;
            }
        }

        builder.setTitle("Cập nhật trạng thái đơn hàng")
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String newStatus = (String) spinnerStatus.getSelectedItem();
                    updateOrderStatus(order, newStatus);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showOrderDetailsDialog(OrderBill order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_order_details, null);
        builder.setView(dialogView);

        // TODO: Fill order details in dialog
        // You can populate TextViews with order information here

        builder.setTitle("Chi tiết đơn hàng: " + order.getOrderBillId())
                .setPositiveButton("Đóng", null)
                .show();
    }

    private void updateOrderStatus(OrderBill order, String newStatus) {
        ordersRef.child(order.getOrderBillId()).child("status").setValue(newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Đã cập nhật trạng thái thành: " + newStatus, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi cập nhật trạng thái: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteOrder(OrderBill order) {
        ordersRef.child(order.getOrderBillId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Đã xóa đơn hàng", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi xóa đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}