package com.example.argapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.argapp.Classes.User;
import com.example.argapp.Controllers.UserController;
import com.example.argapp.Models.UserModel;
import com.example.argapp.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangePasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangePasswordFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChangePasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChangePasswordFragment newInstance(String param1, String param2) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    private View view;

    private EditText currentPassword, newPassword, confirmPassword;
    private Button btnSetPassword;
    private UserController m_UserController;
    private User m_User;

    // Lấy thông tin người dùng hiện tại từ Firebase
    public void GetUser() {
        m_UserController.GetUser(new UserModel.UserCallback() {
            @Override
            public void onSuccess(User user) {
                m_User = user;
                if (m_User != null) {
                    // Hien thi ho ten dung format:
                    String fullName = m_User.getFirstName() + " " + m_User.getLastName();
                }
            }

            @Override
            public void onFailure(DatabaseError error) {
                // Xử lý lỗi khi không thể lấy thông tin người dùng
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Khong the lay thong tin nguoi dung", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        // Thiết lập sự kiện click cho nút back
        ImageView backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_change_password, container, false);

        // ánh xạ
        currentPassword = view.findViewById(R.id.currentPassword);
        newPassword = view.findViewById(R.id.newPassword);
        confirmPassword = view.findViewById(R.id.confirmPassword);
        btnSetPassword = view.findViewById(R.id.btnSetPassword);
        this.m_UserController = new UserController();
        this.m_User = new User();

        GetUser();

        // code
        this.btnSetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });

        backBtn = view.findViewById(R.id.backBtn); // Nếu dùng Fragment
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại trang trước đó
                requireActivity().onBackPressed(); // dùng trong Fragment
                // or
                // onBackPressed(); // nếu trong Activity
            }
        });

        return view;
        // return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    private void changePassword() {
        String currentPwd = currentPassword.getText().toString().trim();
        String newPwd = newPassword.getText().toString().trim();
        String confirmPwd = confirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(currentPwd) || TextUtils.isEmpty(newPwd) || TextUtils.isEmpty(confirmPwd)) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPwd.equals(confirmPwd)) {
            Toast.makeText(getContext(), "Mật khẩu mới và xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) {
            Toast.makeText(getContext(), "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPwd);

        // Xác thực lại
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Cập nhật mật khẩu trong Firebase Authentication
                user.updatePassword(newPwd).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        // Sau khi cập nhật thành công thì lưu mật khẩu mới vào Realtime Database
                        String uid = user.getUid();
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Data/Users").child(uid);
                        userRef.child("password").setValue(newPwd); // không nên lưu plain-text thực tế!

                        Toast.makeText(getContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi đổi mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
            }
        });

    }
}