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

import com.example.argapp.Adapters.AdminReviewsAdapter;
import com.example.argapp.Classes.Review;
import com.example.argapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminReviewsFragment extends Fragment implements AdminReviewsAdapter.OnReviewActionListener {

    private RecyclerView recyclerView;
    private AdminReviewsAdapter adapter;
    private List<Review> reviewsList;
    private List<String> reviewPaths; // To store the path for deletion
    private DatabaseReference reviewsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_reviews, container, false);

        initViews(view);
        setupRecyclerView();
        setupDatabase();
        loadReviews();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_reviews);
    }

    private void setupRecyclerView() {
        reviewsList = new ArrayList<>();
        reviewPaths = new ArrayList<>();
        adapter = new AdminReviewsAdapter(reviewsList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupDatabase() {
        reviewsRef = FirebaseDatabase.getInstance().getReference("Data/Reviews");
    }

    private void loadReviews() {
        reviewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewsList.clear();
                reviewPaths.clear();

                // Navigate through categories -> items -> reviews
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String categoryId = categorySnapshot.getKey();
                    for (DataSnapshot itemSnapshot : categorySnapshot.getChildren()) {
                        String itemId = itemSnapshot.getKey();
                        for (DataSnapshot reviewSnapshot : itemSnapshot.getChildren()) {
                            Review review = reviewSnapshot.getValue(Review.class);
                            if (review != null) {
                                reviewsList.add(review);
                                // Store path for deletion: categoryId/itemId/reviewId
                                String reviewPath = categoryId + "/" + itemId + "/" + reviewSnapshot.getKey();
                                reviewPaths.add(reviewPath);
                            }
                        }
                    }
                }

                // Sort by timestamp (newest first)
                List<Integer> indices = new ArrayList<>();
                for (int i = 0; i < reviewsList.size(); i++) {
                    indices.add(i);
                }
                indices.sort((a, b) -> Long.compare(reviewsList.get(b).getTimestamp(), reviewsList.get(a).getTimestamp()));

                List<Review> sortedReviews = new ArrayList<>();
                List<String> sortedPaths = new ArrayList<>();
                for (int index : indices) {
                    sortedReviews.add(reviewsList.get(index));
                    sortedPaths.add(reviewPaths.get(index));
                }

                reviewsList.clear();
                reviewPaths.clear();
                reviewsList.addAll(sortedReviews);
                reviewPaths.addAll(sortedPaths);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải reviews: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteReview(Review review, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa review này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteReview(position))
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onViewReviewDetails(Review review) {
        showReviewDetailsDialog(review);
    }

    private void showReviewDetailsDialog(Review review) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_review_details, null);
        builder.setView(dialogView);

        // TODO: Fill review details in dialog
        // You can populate TextViews with review information here

        builder.setTitle("Chi tiết Review")
                .setPositiveButton("Đóng", null)
                .show();
    }

    private void deleteReview(int position) {
        if (position < 0 || position >= reviewPaths.size()) {
            Toast.makeText(getContext(), "Lỗi: Không tìm thấy review", Toast.LENGTH_SHORT).show();
            return;
        }

        String reviewPath = reviewPaths.get(position);
        reviewsRef.child(reviewPath).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Đã xóa review", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi xóa review: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}