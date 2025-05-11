package com.example.argapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.argapp.Classes.User;
import com.example.argapp.Controllers.UserController;
import com.example.argapp.Models.UserModel;
import com.example.argapp.R;
import com.google.firebase.database.DatabaseError;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileDetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView userNameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;

    private TextView addressTextView;
    private UserController m_UserController;
    private User m_User;

    public ProfileDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileDetailFragment newInstance(String param1, String param2) {
        ProfileDetailFragment fragment = new ProfileDetailFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile_detail, container, false);

        // Timcac view trong layout
        userNameTextView = view.findViewById(R.id.userName);
        emailTextView = view.findViewById(R.id.email);
        phoneTextView= view.findViewWithTag("phoneNumber");
        addressTextView = view.findViewWithTag("address");

        // Khoi tao UserController
        m_UserController= new UserController();

        //Lấy thong tin nguoi dung tu Firebase
        GetUser();

        ImageView backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });
        ImageView editBtn = view.findViewById(R.id.editBtn);
        editBtn.setOnClickListener(v -> {
            // Chuyển hướng đến EditProfileFragment
            Navigation.findNavController(v).navigate(R.id.action_profileDetail_to_editProfile);
            // Xu ly chuc nang chinh sua thong tin o day
            Toast.makeText(getContext(), "Chức năng chỉnh sửa đang được phát triển", Toast.LENGTH_SHORT).show();
        });
        return view;
        // return inflater.inflate(R.layout.fragment_profile_detail, container, false);
    }

    // Lấy thông tin người dùng từ Firebase
    private void GetUser() {
        m_UserController.GetUser(new UserModel.UserCallback() {
            @Override
            public void onSuccess(User user) {
                m_User = user;

                if (m_User != null) {
                    // Hiển thị thông tin người dùng
                    String fullName = m_User.getFirstName() + " " + m_User.getLastName();
                    userNameTextView.setText(fullName);

                    // Email
                    TextView emailView = view().findViewById(R.id.email);
                    if (emailView != null) {
                        emailView.setText(m_User.getEmail());
                    }

                    // Số điện thoại - tìm TextView trong "Phone" section
                    View phoneLayout = view().findViewWithTag("phoneSection");
                    if (phoneLayout != null) {
                        TextView phoneValue = phoneLayout.findViewById(R.id.phoneValue);
                        if (phoneValue != null) {
                            phoneValue.setText(m_User.getPhoneNumber());
                        }
                    }

                    // Địa chỉ - tìm TextView trong "Address" section
                    View addressLayout = view().findViewWithTag("addressSection");
                    if (addressLayout != null) {
                        TextView addressValue = addressLayout.findViewById(R.id.addressValue);
                        if (addressValue != null) {
                            String address = m_User.getM_address();
                            addressValue.setText(address != null ? address : "Chưa cập nhật địa chỉ");
                        }
                    }
                }
            }

            @Override
            public void onFailure(DatabaseError error) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Không thể tải thông tin chi tiết người dùng: " +
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    // Helper method để tránh null pointer exception
    private View view() {
        return getView();
    }
}