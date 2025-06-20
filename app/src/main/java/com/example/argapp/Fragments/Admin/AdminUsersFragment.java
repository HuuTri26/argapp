package com.example.argapp.Fragments.Admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argapp.Adapters.AdminUsersAdapter;
import com.example.argapp.Classes.User;
import com.example.argapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminUsersFragment extends Fragment implements AdminUsersAdapter.OnUserActionListener {

    private RecyclerView recyclerView;
    private AdminUsersAdapter adapter;
    private List<UserWithId> usersList;
    private DatabaseReference usersRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_users, container, false);

        initViews(view);
        setupRecyclerView();
        setupDatabase();
        loadUsers();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_users);
    }

    private void setupRecyclerView() {
        usersList = new ArrayList<>();
        adapter = new AdminUsersAdapter(usersList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupDatabase() {
        usersRef = FirebaseDatabase.getInstance().getReference("Data/Users");
    }

    private void loadUsers() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        String userId = userSnapshot.getKey();
                        usersList.add(new UserWithId(userId, user));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải users: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewUserDetails(UserWithId userWithId) {
        showUserDetailsDialog(userWithId);
    }

    @Override
    public void onDeleteUser(UserWithId userWithId) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa user: " + userWithId.user.getFirstName() + " " + userWithId.user.getLastName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteUser(userWithId))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showUserDetailsDialog(UserWithId userWithId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_user_details, null);
        builder.setView(dialogView);

        // TODO: Fill user details in dialog
        // You can populate TextViews with user information here

        builder.setTitle("Chi tiết User: " + userWithId.user.getFirstName() + " " + userWithId.user.getLastName())
                .setPositiveButton("Đóng", null)
                .show();
    }

    private void deleteUser(UserWithId userWithId) {
        usersRef.child(userWithId.userId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Đã xóa user", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi xóa user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Helper class to store user with ID
    public static class UserWithId {
        public String userId;
        public User user;

        public UserWithId(String userId, User user) {
            this.userId = userId;
            this.user = user;
        }
    }
}