package com.example.argapp.Fragments.Admin;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argapp.Adapters.AdminCouponsAdapter;
import com.example.argapp.Classes.Coupon;
import com.example.argapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminCouponsFragment extends Fragment implements AdminCouponsAdapter.OnCouponActionListener {

    private RecyclerView recyclerView;
    private AdminCouponsAdapter adapter;
    private List<Coupon> couponsList;
    private DatabaseReference couponsRef;
    private FloatingActionButton fabAddCoupon;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_coupons, container, false);

        initViews(view);
        setupRecyclerView();
        setupDatabase();
        loadCoupons();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_coupons);
        fabAddCoupon = view.findViewById(R.id.fab_add_coupon);

        fabAddCoupon.setOnClickListener(v -> showAddCouponDialog());
    }

    private void setupRecyclerView() {
        couponsList = new ArrayList<>();
        adapter = new AdminCouponsAdapter(couponsList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupDatabase() {
        couponsRef = FirebaseDatabase.getInstance().getReference("Data/Coupons");
    }

    private void loadCoupons() {
        couponsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                couponsList.clear();
                for (DataSnapshot couponSnapshot : snapshot.getChildren()) {
                    String couponId = couponSnapshot.child("Id").getValue(String.class);
                    String productId = couponSnapshot.child("productId").getValue(String.class);
                    String type = couponSnapshot.child("couponType").getValue(String.class);
                    Double discountValue = couponSnapshot.child("discountValue").getValue(Double.class);
                    String startDateStr = couponSnapshot.child("startDate").getValue(String.class);
                    String endDateStr = couponSnapshot.child("endDate").getValue(String.class);
                    String description = couponSnapshot.child("description").getValue(String.class);

                    Date startDate = null;
                    Date endDate = null;

                    try {
                        if (startDateStr != null) {
                            startDate = dateFormat.parse(startDateStr);
                        }
                        if (endDateStr != null) {
                            endDate = dateFormat.parse(endDateStr);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Coupon coupon = new Coupon(couponId, productId, type, discountValue,
                            startDate, endDate, description);
                    couponsList.add(coupon);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải coupons: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddCouponDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_edit_coupon, null);
        builder.setView(dialogView);

        EditText etId = dialogView.findViewById(R.id.et_coupon_id);
        EditText etProductId = dialogView.findViewById(R.id.et_product_id);
        EditText etDiscountValue = dialogView.findViewById(R.id.et_discount_value);
        EditText etDescription = dialogView.findViewById(R.id.et_description);
        EditText etStartDate = dialogView.findViewById(R.id.et_start_date);
        EditText etEndDate = dialogView.findViewById(R.id.et_end_date);
        Spinner spinnerType = dialogView.findViewById(R.id.spinner_coupon_type);

        // Setup type spinner
        String[] types = {"percentage", "fixed"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        // Date pickers
        setupDatePicker(etStartDate);
        setupDatePicker(etEndDate);

        builder.setTitle("Thêm Coupon mới")
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String id = etId.getText().toString().trim();
                    String productId = etProductId.getText().toString().trim();
                    String discountStr = etDiscountValue.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    String startDateStr = etStartDate.getText().toString().trim();
                    String endDateStr = etEndDate.getText().toString().trim();
                    String type = (String) spinnerType.getSelectedItem();

                    if (id.isEmpty() || discountStr.isEmpty() || startDateStr.isEmpty() || endDateStr.isEmpty()) {
                        Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double discountValue = Double.parseDouble(discountStr);
                        addCoupon(id, productId, type, discountValue, description, startDateStr, endDateStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Giá trị giảm giá không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void setupDatePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, year, month, dayOfMonth) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        editText.setText(dateFormat.format(selectedDate.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void addCoupon(String id, String productId, String type, double discountValue,
                           String description, String startDate, String endDate) {
        Map<String, Object> couponData = new HashMap<>();
        couponData.put("Id", id);
        couponData.put("productId", productId);
        couponData.put("couponType", type);
        couponData.put("discountValue", discountValue);
        couponData.put("description", description);
        couponData.put("startDate", startDate);
        couponData.put("endDate", endDate);

        couponsRef.child(id).setValue(couponData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Đã thêm coupon thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi thêm coupon: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onEditCoupon(Coupon coupon) {
        showEditCouponDialog(coupon);
    }

    private void showEditCouponDialog(Coupon coupon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_edit_coupon, null);
        builder.setView(dialogView);

        EditText etId = dialogView.findViewById(R.id.et_coupon_id);
        EditText etProductId = dialogView.findViewById(R.id.et_product_id);
        EditText etDiscountValue = dialogView.findViewById(R.id.et_discount_value);
        EditText etDescription = dialogView.findViewById(R.id.et_description);
        EditText etStartDate = dialogView.findViewById(R.id.et_start_date);
        EditText etEndDate = dialogView.findViewById(R.id.et_end_date);
        Spinner spinnerType = dialogView.findViewById(R.id.spinner_coupon_type);

        // Fill current data
        etId.setText(coupon.getId());
        etId.setEnabled(false); // ID should not be editable
        etProductId.setText(coupon.getProductId());
        etDiscountValue.setText(String.valueOf(coupon.getDiscountValue()));
        etDescription.setText(coupon.getDescription());

        if (coupon.getStartDate() != null) {
            etStartDate.setText(dateFormat.format(coupon.getStartDate()));
        }
        if (coupon.getEndDate() != null) {
            etEndDate.setText(dateFormat.format(coupon.getEndDate()));
        }

        // Setup type spinner
        String[] types = {"percentage", "fixed"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        // Set current type
        for (int i = 0; i < types.length; i++) {
            if (types[i].equals(coupon.getType())) {
                spinnerType.setSelection(i);
                break;
            }
        }

        // Date pickers
        setupDatePicker(etStartDate);
        setupDatePicker(etEndDate);

        builder.setTitle("Chỉnh sửa Coupon")
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String productId = etProductId.getText().toString().trim();
                    String discountStr = etDiscountValue.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    String startDateStr = etStartDate.getText().toString().trim();
                    String endDateStr = etEndDate.getText().toString().trim();
                    String type = (String) spinnerType.getSelectedItem();

                    if (discountStr.isEmpty() || startDateStr.isEmpty() || endDateStr.isEmpty()) {
                        Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double discountValue = Double.parseDouble(discountStr);
                        updateCoupon(coupon.getId(), productId, type, discountValue, description, startDateStr, endDateStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Giá trị giảm giá không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void updateCoupon(String id, String productId, String type, double discountValue,
                              String description, String startDate, String endDate) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("productId", productId);
        updates.put("couponType", type);
        updates.put("discountValue", discountValue);
        updates.put("description", description);
        updates.put("startDate", startDate);
        updates.put("endDate", endDate);

        couponsRef.child(id).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Đã cập nhật coupon", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDeleteCoupon(Coupon coupon) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa coupon: " + coupon.getId() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteCoupon(coupon))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteCoupon(Coupon coupon) {
        couponsRef.child(coupon.getId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Đã xóa coupon", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi xóa coupon: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}