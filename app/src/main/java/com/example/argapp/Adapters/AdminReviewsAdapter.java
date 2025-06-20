package com.example.argapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class AdminReviewsAdapter extends RecyclerView.Adapter<AdminReviewsAdapter.ViewHolder> {

    private List<Review> reviewsList;
    private OnReviewActionListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public interface OnReviewActionListener {
        void onDeleteReview(Review review, int position);
        void onViewReviewDetails(Review review);
    }

    public AdminReviewsAdapter(List<Review> reviewsList, OnReviewActionListener listener) {
        this.reviewsList = reviewsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviewsList.get(position);

        holder.tvUserName.setText(review.getUserName());
        holder.tvComment.setText(review.getComment());
        holder.ratingBar.setRating(review.getRating());
        holder.tvDate.setText(dateFormat.format(new Date(review.getTimestamp())));

        holder.btnViewDetails.setOnClickListener(v -> listener.onViewReviewDetails(review));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteReview(review, position));
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvComment, tvDate;
        RatingBar ratingBar;
        Button btnViewDetails, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvComment = itemView.findViewById(R.id.tv_comment);
            tvDate = itemView.findViewById(R.id.tv_date);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}