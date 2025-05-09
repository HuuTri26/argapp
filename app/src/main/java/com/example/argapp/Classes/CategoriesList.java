package com.example.argapp.Classes;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.argapp.Interfaces.OnCategoriesFetchedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp CategoriesList cung cấp phương thức để truy vấn và lấy danh sách các danh mục từ Firebase
 * Sử dụng mô hình callback để trả dữ liệu về sau khi hoàn tất quá trình truy vấn bất đồng bộ
 */
public class CategoriesList {

    // Danh sách lưu trữ các đối tượng Category được lấy từ Firebase
    private static List<Category> m_CategoriesList = new ArrayList<>();

    /**
     * Phương thức để lấy danh sách danh mục từ Firebase Realtime Database
     * @param context Context để hiển thị thông báo lỗi nếu cần
     * @param callback Interface callback để trả về kết quả sau khi truy vấn thành công hoặc thất bại
     */
    public static void GetCategoriesList(Context context, OnCategoriesFetchedListener callback)
    {
        // Khởi tạo tham chiếu đến Firebase Database
        FirebaseDatabase m_Database;
        DatabaseReference m_Ref;

        // Lấy instance của Firebase Database
        m_Database = FirebaseDatabase.getInstance();
        // Chỉ định đường dẫn đến node "Data/Categories" trong database
        m_Ref = m_Database.getReference("Data/Categories");

        // Xóa danh sách cũ để tránh trùng lặp dữ liệu khi tải lại
        m_CategoriesList.clear();
        
        // Thêm listener một lần để lấy dữ liệu từ Firebase
        // addListenerForSingleValueEvent sẽ chỉ lấy dữ liệu một lần, không lắng nghe các thay đổi
        m_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            /**
             * Được gọi khi dữ liệu được đọc thành công từ Firebase
             * @param snapshot Chứa toàn bộ dữ liệu từ đường dẫn đã chỉ định
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Duyệt qua tất cả các con (category) trong snapshot
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    // Lấy các thuộc tính của category từ Firebase
                    String categoryId = itemSnapshot.child("Id").getValue(String.class);
                    String categoryName = itemSnapshot.child("Name").getValue(String.class);
                    String categoryImage = itemSnapshot.child("Image").getValue(String.class);
                    System.out.println(categoryImage);
                    Log.e("A",categoryImage);
                    // Tạo đối tượng Category mới với dữ liệu vừa lấy
                    Category category = new Category(categoryId, categoryName, categoryImage);
                    // Thêm vào danh sách các danh mục
                    m_CategoriesList.add(category);
                }

                // Gọi callback khi quá trình lấy dữ liệu hoàn tất thành công
                // và trả về danh sách đã được điền đầy đủ thông tin
                callback.onCategoriesFetched(m_CategoriesList);
            }

            /**
             * Được gọi khi có lỗi xảy ra trong quá trình đọc dữ liệu
             * @param error Chứa thông tin về lỗi đã xảy ra
             */
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Hiển thị thông báo lỗi cho người dùng
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                // Gọi callback với danh sách trống để thông báo việc không lấy được dữ liệu
                callback.onCategoriesFetched(new ArrayList<>());
            }
        });
    }
}