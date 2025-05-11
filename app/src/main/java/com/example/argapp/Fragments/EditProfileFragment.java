package com.example.argapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.argapp.Classes.User;
import com.example.argapp.Controllers.UserController;
import com.example.argapp.Models.UserModel;
import com.example.argapp.R;
import com.google.firebase.database.DatabaseError;

public class EditProfileFragment extends Fragment {
    private EditText lastNameInput;
    private EditText firstNameInput;
    private EditText phoneNumberInput;
    private EditText addressInput;
    private Button saveBtn;
    private ImageView backBtn;

    private UserController userController;
    private User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Ánh xạ views
        lastNameInput = view.findViewById(R.id.lastNameInput);
        firstNameInput = view.findViewById(R.id.firstNameInput);
        phoneNumberInput = view.findViewById(R.id.phoneNumberInput);
        addressInput = view.findViewById(R.id.addressInput);
        saveBtn = view.findViewById(R.id.saveBtn);
        backBtn = view.findViewById(R.id.backBtn);

        // Khởi tạo UserController
        userController = new UserController();

        // Lấy thông tin người dùng hiện tại
        fetchUserData();

        // Thiết lập sự kiện cho nút Back
        backBtn.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        // Thiết lập sự kiện cho nút Save
        saveBtn.setOnClickListener(v -> {
            saveUserData();
        });

        return view;
    }

    // Lấy thông tin người dùng từ Firebase
    private void fetchUserData() {
        userController.GetUser(new UserModel.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (user != null && getActivity() != null) {
                    currentUser = user;

                    // Điền thông tin hiện có vào các trường nhập liệu
                    lastNameInput.setText(user.getLastName());
                    firstNameInput.setText(user.getFirstName());
                    phoneNumberInput.setText(user.getPhoneNumber());

                    // Điền địa chỉ nếu có
                    String address = user.getM_address();
                    if (address != null && !address.isEmpty()) {
                        addressInput.setText(address);
                    }
                }
            }

            @Override
            public void onFailure(DatabaseError error) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Không thể tải thông tin người dùng: " +
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Lưu thông tin người dùng xuống Firebase
    private void saveUserData() {
        // Lấy giá trị từ các trường nhập liệu
        String lastName = lastNameInput.getText().toString().trim();
        String firstName = firstNameInput.getText().toString().trim();
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();

        // Kiểm tra dữ liệu
        if (lastName.isEmpty()) {
            lastNameInput.setError("Vui lòng nhập họ");
            return;
        }

        if (firstName.isEmpty()) {
            firstNameInput.setError("Vui lòng nhập tên");
            return;
        }

        if (phoneNumber.isEmpty()) {
            phoneNumberInput.setError("Vui lòng nhập số điện thoại");
            return;
        }

        // Hiển thị thông báo đang cập nhật
        Toast.makeText(getActivity(), "Đang cập nhật thông tin...", Toast.LENGTH_SHORT).show();

        // Gọi UserController để cập nhật thông tin
        userController.updateUserProfile(firstName, lastName, phoneNumber, address,
                new UserModel.UpdateProfileCallback() {
                    @Override
                    public void onSuccess() {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), "Cập nhật thông tin thành công",
                                    Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(getView()).popBackStack();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), "Cập nhật thông tin thất bại: " +
                                    e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
