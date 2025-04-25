package com.example.argapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.argapp.R;

/**
 * Màn hình khởi động (Splash Screen) của ứng dụng
 * Hiển thị trong một khoảng thời gian ngắn trước khi chuyển sang MainActivity
 */
public class SplashActivity extends AppCompatActivity {

    // Thời gian hiển thị màn hình Splash (tính bằng mili giây)
    private static final int SPLASH_TIMEOUT = 2000; // 2 giây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Gọi phương thức của lớp cha
        EdgeToEdge.enable(this); // Kích hoạt chế độ hiển thị Edge-to-Edge cho giao diện đẹp hơn
        setContentView(R.layout.activity_splash); // Thiết lập layout cho activity
        
        // Thiết lập listener để xử lý window insets cho việc hiển thị edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // Lấy insets cho các thanh hệ thống (status bar, navigation bar)
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Thiết lập padding cho view dựa trên các insets để tránh nội dung bị che khuất
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Tạo một Handler để trì hoãn việc chuyển sang MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Tạo và khởi chạy Intent để chuyển đến MainActivity
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish(); // Đóng SplashActivity để người dùng không thể quay lại bằng nút Back
            }
        }, SPLASH_TIMEOUT); // Trì hoãn trong khoảng thời gian SPLASH_TIMEOUT (2 giây)
    }
}