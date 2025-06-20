package com.example.argapp.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.argapp.Adapters.OrderBillAdapter;
import com.example.argapp.Classes.OrderBill;
import com.example.argapp.Controllers.UserController;
import com.example.argapp.Models.UserModel;
import com.example.argapp.R;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryFragment extends Fragment implements OrderBillAdapter.OnOrderBillClickListener {
    private View view;
    private List<OrderBill> orderBillList;
    private RecyclerView orderBillRecyclerView;
    private OrderBillAdapter orderBillAdapter;
    private UserController userController;
    private ProgressBar progressBar;
    private TextView emptyView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userController = new UserController();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_order_history, container, false);

        // Khởi tạo views
        this.orderBillRecyclerView = view.findViewById(R.id.recyclerViewOrderBills);
        this.progressBar = view.findViewById(R.id.progressBar);
        this.emptyView = view.findViewById(R.id.emptyView);
        
        // Khởi tạo adapter với this làm listener
        this.orderBillList = new ArrayList<>();
        this.orderBillAdapter = new OrderBillAdapter(this.orderBillList, getContext(), this);
        
        // Thiết lập RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        this.orderBillRecyclerView.setLayoutManager(linearLayoutManager);
        this.orderBillRecyclerView.setAdapter(this.orderBillAdapter);

        // Thiết lập nút back
        ImageView backBtn = view.findViewById(R.id.btnBack);
        backBtn.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        // Tải dữ liệu đơn hàng
        loadOrderHistory();

        return view;
    }

    private void loadOrderHistory() {
        // Hiển thị ProgressBar
        progressBar.setVisibility(View.VISIBLE);
        orderBillRecyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        
        // Lấy danh sách đơn hàng
        userController.getUserOrderBills(new UserModel.OrderBillsCallback() {
            @Override
            public void onSuccess(List<OrderBill> orderBills) {
                // Ẩn ProgressBar
                progressBar.setVisibility(View.GONE);
                
                // Cập nhật danh sách đơn hàng
                orderBillList.clear();
                if (orderBills != null) {
                    orderBillList.addAll(orderBills);
                }
                
                // Cập nhật adapter
                orderBillAdapter.notifyDataSetChanged();
                
                // Hiển thị thông báo nếu không có đơn hàng
                if (orderBillList.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                    orderBillRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    orderBillRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(DatabaseError error) {
                // Ẩn ProgressBar
                progressBar.setVisibility(View.GONE);
                
                // Hiển thị thông báo lỗi
                Toast.makeText(getContext(), "Lỗi khi tải lịch sử đơn hàng", Toast.LENGTH_SHORT).show();
                
                // Hiển thị view trống
                emptyView.setText("Không thể tải dữ liệu. Vui lòng thử lại sau.");
                emptyView.setVisibility(View.VISIBLE);
                orderBillRecyclerView.setVisibility(View.GONE);
            }
        });
    }
    
    @Override
    public void onOrderBillClick(OrderBill orderBill) {
        // Chuyển sang màn hình chi tiết đơn hàng
        Bundle args = new Bundle();
        args.putString("orderBillId", orderBill.getOrderBillId());
        Navigation.findNavController(view).navigate(R.id.action_orderBillHistory_to_orderDetailFragment, args);
    }

    public void onCancelOrderClick(OrderBill orderBill, int position) {
        // Kiểm tra trạng thái đơn hàng
        if (!"PENDING".equals(orderBill.getStatus())) {
            Toast.makeText(getContext(), "Chỉ có thể hủy đơn hàng đang chờ xử lý", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị dialog xác nhận hủy đơn hàng
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận hủy đơn hàng")
                .setMessage("Bạn có chắc chắn muốn hủy đơn hàng #" + orderBill.getOrderBillId() + " không?")
                .setPositiveButton("Hủy đơn hàng", (d, which) -> {
                    cancelOrder(orderBill, position);
                })
                .setNegativeButton("Không", null)
                .create();

        dialog.show();

        // Đặt màu cho các nút
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(android.graphics.Color.parseColor("#4CAF50")); // Màu xanh lá cây
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(android.graphics.Color.parseColor("#F44336")); // Màu đỏ
    }

    private void cancelOrder(OrderBill orderBill, int position) {
        // Hiển thị loading
        progressBar.setVisibility(View.VISIBLE);

        userController.cancelOrder(orderBill.getOrderBillId(), new UserModel.CancelOrderCallback() {
            @Override
            public void onSuccess() {
                // Ẩn loading
                progressBar.setVisibility(View.GONE);

                // Cập nhật trạng thái đơn hàng trong adapter
                orderBillAdapter.updateOrderStatus(position, "CANCELLED");

                // Hiển thị thông báo thành công
                Toast.makeText(getContext(), "Đã hủy đơn hàng thành công", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception error) {
                // Ẩn loading
                progressBar.setVisibility(View.GONE);

                // Hiển thị thông báo lỗi
                String errorMessage = "Không thể hủy đơn hàng";
                if (error != null && error.getMessage() != null) {
                    errorMessage += ": " + error.getMessage();
                }
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}