package com.example.argapp.Classes;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.argapp.Interfaces.OnCouponsFetchedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Lớp CouponList cung cấp phương thức để truy vấn và lấy danh sách các coupon từ Firebase
 * Sử dụng mô hình callback để trả dữ liệu về sau khi hoàn tất quá trình truy vấn bất đồng bộ
 */
public class CouponList {
    private static List<Coupon> m_CouponsList = new ArrayList<>();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    /**
     * Phương thức để lấy danh sách coupon từ Firebase Realtime Database
     *
     * @param context    Context để hiển thị thông báo lỗi nếu cần
     * @param i_CouponId ID của coupon cần truy vấn
     * @param callback   Interface callback để trả về kết quả sau khi truy vấn thành công hoặc thất bại
     */
    public void getCouponsByCouponId(Context context, String i_CouponId, OnCouponsFetchedListener callback) {
        // Khởi tạo tham chiếu đến Firebase Database
        FirebaseDatabase m_Database;
        DatabaseReference m_Ref;

        // Lấy instance của Firebase Database
        m_Database = FirebaseDatabase.getInstance();
        // Chỉ định đường dẫn đến node "Data/Coupons" trong database
        m_Ref = m_Database.getReference("Data/Coupons");

        // Xóa danh sách cũ để tránh trùng lặp dữ liệu khi tải lại
        m_CouponsList.clear();

        m_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String couponId = dataSnapshot.child("Id").getValue(String.class);
//                    System.out.println("Coupon ID: " + couponId);
                    Log.d("CouponList", "Coupon ID: " + couponId);

                    if (couponId != null && couponId.equals(i_CouponId)) {
                        String productId = dataSnapshot.child("productId").getValue(String.class);
                        String type = dataSnapshot.child("couponType").getValue(String.class);
                        Double discountValue = dataSnapshot.child("discountValue").getValue(Double.class);
                        String startDateStr = dataSnapshot.child("startDate").getValue(String.class);
                        String endDateStr = dataSnapshot.child("endDate").getValue(String.class);
                        String description = dataSnapshot.child("description").getValue(String.class);

                        Date startDate = null;
                        Date endDate = null;

                        try {
                            if (startDateStr != null) {
                                startDate = DATE_FORMAT.parse(startDateStr);
                            }
                            if (endDateStr != null) {
                                endDate = DATE_FORMAT.parse(endDateStr);
                            }
                        } catch (ParseException e) {
                            System.err.println("Error parsing date: " + e.getMessage());
                        }

                        Coupon coupon = new Coupon(couponId, productId, type, discountValue,
                                startDate, endDate, description);

                        m_CouponsList.add(coupon);
                        break; // Thoát vòng lặp khi tìm thấy coupon
                    }
                }

                // Gọi callback sau khi hoàn thành việc tìm kiếm
                callback.onCouponsFetched(m_CouponsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                callback.onCouponsFetched(new ArrayList<>());
            }
        });
    }
}
