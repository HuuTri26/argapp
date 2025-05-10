package com.example.argapp.Fragments;

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
import com.google.firebase.auth.FirebaseUser;
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
                if (user != null) {
                    textViewHoTen.setText(user.getFirstName() + " " + user.getLastName());
                    textViewEmail.setText(user.getEmail());
                } else {
                    Toast.makeText(getContext(), "Lỗi lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

        this.m_UserController = new UserController();
        GetUser();
//        this.m_HostedActivity.GetUser();

        LinearLayout layoutProfileDetail = view.findViewById(R.id.layout_profile_detail);

        LinearLayout layoutChangePassword = view.findViewById(R.id.layoutChangePassword);

        LinearLayout layoutOrderHistory = view.findViewById(R.id.layoutOrderHistory);

        layoutProfileDetail.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_profilePage_to_profile_detail);
        });

        layoutChangePassword.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_profilePage_to_changePassword);
        });

        layoutOrderHistory.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_orderBillFragment);
        });

        return view;
        // return inflater.inflate(R.layout.fragment_profile_page, container, false);
    }


}