package com.example.argapp.Fragments.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

import com.example.argapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminDashboardFragment extends Fragment {

    private TextView tvTotalUsers, tvTotalOrders, tvTotalItems, tvTotalCategories;
    private CardView cardUsers, cardOrders, cardItems, cardCategories;
    private DatabaseReference databaseRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        initViews(view);
        setupDatabase();
        loadDashboardData();

        return view;
    }

    private void initViews(View view) {
        tvTotalUsers = view.findViewById(R.id.tv_total_users);
        tvTotalOrders = view.findViewById(R.id.tv_total_orders);
        tvTotalItems = view.findViewById(R.id.tv_total_items);
        tvTotalCategories = view.findViewById(R.id.tv_total_categories);

        cardUsers = view.findViewById(R.id.card_users);
        cardOrders = view.findViewById(R.id.card_orders);
        cardItems = view.findViewById(R.id.card_items);
        cardCategories = view.findViewById(R.id.card_categories);
    }

    private void setupDatabase() {
        databaseRef = FirebaseDatabase.getInstance().getReference("Data");
    }

    private void loadDashboardData() {
        // Đếm số Users
        databaseRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long userCount = snapshot.getChildrenCount();
                tvTotalUsers.setText(String.valueOf(userCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvTotalUsers.setText("0");
            }
        });

        // Đếm số Orders
        databaseRef.child("OrderBills").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long orderCount = snapshot.getChildrenCount();
                tvTotalOrders.setText(String.valueOf(orderCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvTotalOrders.setText("0");
            }
        });

        // Đếm số Items
        databaseRef.child("CategoryItems").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long itemCount = 0;
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    itemCount += categorySnapshot.getChildrenCount();
                }
                tvTotalItems.setText(String.valueOf(itemCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvTotalItems.setText("0");
            }
        });

        // Đếm số Categories
        databaseRef.child("Categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long categoryCount = snapshot.getChildrenCount();
                tvTotalCategories.setText(String.valueOf(categoryCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvTotalCategories.setText("0");
            }
        });
    }
}