package com.example.argapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argapp.Activities.MainActivity;
import com.example.argapp.Adapters.OrderItemAdapter;
import com.example.argapp.Classes.OrderBill;
import com.example.argapp.Classes.OrderBillItem;
import com.example.argapp.Models.UserModel;
import com.example.argapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderDetailFragment extends Fragment {
    private String orderBillId;
    private View view;
    private TextView tvOrderId, tvOrderDate, tvStatus, tvTotalPrice;
    private RecyclerView recyclerViewItems;
    private ProgressBar progressBar;
    private OrderItemAdapter orderItemAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderBillId = getArguments().getString("orderBillId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_detail, container, false);

        // Khởi tạo views
        tvOrderId = view.findViewById(R.id.tvDetailOrderId);
        tvOrderDate = view.findViewById(R.id.tvDetailOrderDate);
        tvStatus = view.findViewById(R.id.tvDetailStatus);
        tvTotalPrice = view.findViewById(R.id.tvDetailTotalPrice);
        recyclerViewItems = view.findViewById(R.id.recyclerViewOrderItems);
        progressBar = view.findViewById(R.id.progressBarDetail);

        // Thiết lập RecyclerView
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(getContext()));

        // Thiết lập nút back
        ImageView backBtn = view.findViewById(R.id.btnDetailBack);
        backBtn.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        // Load chi tiết đơn hàng
        if (orderBillId != null) {
            loadOrderDetails(orderBillId);
        } else {
            Toast.makeText(getContext(), "Không tìm thấy thông tin đơn hàng", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).popBackStack();
        }

        return view;
    }

    private void loadOrderDetails(String orderBillId) {
        progressBar.setVisibility(View.VISIBLE);
        
        // Gọi phương thức lấy chi tiết đơn hàng
        ((MainActivity) requireActivity()).getUserController().getOrderDetail(orderBillId, new UserModel.OrderDetailCallback() {
            @Override
            public void onSuccess(OrderBill orderBill) {
                progressBar.setVisibility(View.GONE);
                
                if (orderBill != null) {
                    displayOrderDetails(orderBill);
                } else {
                    Toast.makeText(getContext(), "Không tìm thấy thông tin đơn hàng", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).popBackStack();
                }
            }
            
            @Override
            public void onFailure(Exception error) {
                progressBar.setVisibility(View.GONE);
                
                Toast.makeText(getContext(), "Lỗi khi tải chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).popBackStack();
            }
        });
    }
    
    private void displayOrderDetails(OrderBill orderBill) {
        // Hiển thị thông tin đơn hàng
        tvOrderId.setText("Đơn hàng #" + orderBill.getOrderBillId());
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        tvOrderDate.setText("Ngày đặt: " + sdf.format(new Date(orderBill.getOrderDate())));
        
        tvStatus.setText("Trạng thái: " + orderBill.getStatus());
        tvTotalPrice.setText("Tổng tiền: " + String.format("%.2f VNĐ", orderBill.getTotalPrice()));
        
        // Hiển thị danh sách sản phẩm trong đơn hàng
        if (orderBill.getItems() != null) {
            List<OrderBillItem> itemList = new ArrayList<>(orderBill.getItems().values());
            orderItemAdapter = new OrderItemAdapter(itemList, getContext());
            recyclerViewItems.setAdapter(orderItemAdapter);
        }
    }
}