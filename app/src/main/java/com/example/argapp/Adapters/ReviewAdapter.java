package com.example.argapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.argapp.Classes.Review;
import com.example.argapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviews;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);

        // Hiển thị tên người dùng
        holder.reviewerName.setText(review.getUserName());

        // Thiết lập rating
        holder.reviewRating.setRating(review.getRating());

        // Hiển thị ngày đánh giá từ timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date(review.getTimestamp());
        holder.reviewDate.setText(dateFormat.format(date));

        // Hiển thị nội dung đánh giá
        holder.reviewContent.setText(review.getComment());

        // Hiển thị ảnh đại diện (chuyển đổi từ chuỗi "drawable/xxx" sang resource ID)
        if (review.getUserImage() != null && review.getUserImage().startsWith("drawable/")) {
            String resourceName = review.getUserImage().replace("drawable/", "");
            int resourceId = holder.itemView.getContext().getResources()
                    .getIdentifier(resourceName, "drawable", holder.itemView.getContext().getPackageName());

            if (resourceId != 0) {
                holder.reviewerImage.setImageResource(resourceId);
            } else {
                holder.reviewerImage.setImageResource(R.drawable.apple);
            }
        } else {
            holder.reviewerImage.setImageResource(R.drawable.apple);
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void updateReviews(List<Review> newReviews) {
        this.reviews = newReviews;
        notifyDataSetChanged();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView reviewerImage;
        TextView reviewerName;
        RatingBar reviewRating;
        TextView reviewDate;
        TextView reviewContent;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewerImage = itemView.findViewById(R.id.reviewerImage);
            reviewerName = itemView.findViewById(R.id.reviewerName);
            reviewRating = itemView.findViewById(R.id.reviewRating);
            reviewDate = itemView.findViewById(R.id.reviewDate);
            reviewContent = itemView.findViewById(R.id.reviewContent);
        }
    }
}
