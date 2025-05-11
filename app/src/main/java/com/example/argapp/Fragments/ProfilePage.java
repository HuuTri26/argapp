package com.example.argapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.argapp.Activities.MainActivity;
import com.example.argapp.Classes.User;
import com.example.argapp.Controllers.UserController;
import com.example.argapp.Models.UserModel;
import com.example.argapp.R;
import com.google.firebase.database.DatabaseError;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfilePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfilePage extends Fragment {

    private TextView textViewHoTen;
    private TextView textViewEmail;

    private UserController m_UserController; // Biến điều khiển người dùng, xử lý tương tác với Firebase
    //    private MainActivity m_HostedActivity;
    private User m_User;  // Đối tượng chứa thông tin người dùng hiện tại

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfilePage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfilePage.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfilePage newInstance(String param1, String param2) {
        ProfilePage fragment = new ProfilePage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // Lấy thông tin người dùng hiện tại từ Firebase
    public void GetUser() {
        m_UserController.GetUser(new UserModel.UserCallback() {
            @Override
            public void onSuccess(User user) {
                m_User = user;
                if(m_User != null){
                    // Hien thi ho ten dung format:
                    String fullName = m_User.getFirstName() + " " + m_User.getLastName();
                    textViewHoTen.setText(fullName);
                    textViewEmail.setText(m_User.getEmail());

                }
            }

            @Override
            public void onFailure(DatabaseError error) {
                // Xử lý lỗi khi không thể lấy thông tin người dùng
                if(getActivity() != null){
                    Toast.makeText(getActivity(),"Khong the lay thong tin nguoi dung",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_page, container, false);

//        this.m_HostedActivity = (MainActivity) requireActivity();

        this.textViewHoTen = view.findViewById(R.id.userName);
        this.textViewEmail = view.findViewById(R.id.email);
        //Khởi tạo  Usercontroller để lấy dữ lệu người dùng
        this.m_UserController = new UserController();

        //Lấy thông tin người dùng
        GetUser();
//        this.m_HostedActivity.GetUser();

        LinearLayout layoutProfileDetail = view.findViewById(R.id.layout_profile_detail);

        LinearLayout layoutChangePassword = view.findViewById(R.id.layoutChangePassword);

        LinearLayout layoutOrderHistory = view.findViewById(R.id.layoutOrderHistory);

        LinearLayout layoutLogout = view.findViewById(R.id.layoutLogout);

        layoutProfileDetail.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_profilePage_to_profile_detail);
        });

        layoutChangePassword.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_profilePage_to_changePassword);
        });

        layoutOrderHistory.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_orderBillFragment);
        });

        // Xư lý s kiện click nút đăng xuất
        layoutLogout.setOnClickListener(v -> {
            showLogoutConfirmationDialog();
        });

        return view;
        // return inflater.inflate(R.layout.fragment_profile_page, container, false);
    }

    // Thêm phương thức để hiển thị hộp thoại xác nhận đăng xuất
    private void showLogoutConfirmationDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Xác nhận đăng xuất");
        builder.setMessage("Bạn có chắc chắn muốn đăng xuất?");
        
        // Tạo nút xác nhận với màu xanh lá
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            // Đăng xuất khỏi hệ thống
            performLogout();
        });
        
        // Tạo nút hủy với màu đỏ
        builder.setNegativeButton("Hủy", (dialog, which) -> {
            dialog.dismiss();
        });
        
        // Tạo và hiển thị hộp thoại
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
        
        // Đặt màu cho các nút sau khi hiển thị dialog
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
              .setTextColor(android.graphics.Color.parseColor("#4CAF50")); // Màu xanh lá cây
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
              .setTextColor(android.graphics.Color.parseColor("#F44336")); // Màu đỏ
    }

    // Phương thức để thực hiện đăng xuất
    private void performLogout() {
        try {
            // Đăng xuất khỏi Firebase Auth
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut();

            // Xóa thông tin đăng nhập đã lưu
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Sử dụng Intent để khởi động lại ứng dụng ở trang Login
            Intent intent = new Intent(requireActivity(), requireActivity().getClass());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();

            // Thông báo đăng xuất thành công
            Toast.makeText(requireContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Lỗi đăng xuất: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    // Cập nhật phương tức GetUsser hiển thị dũ liệu :

}