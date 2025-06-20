package com.example.argapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argapp.Fragments.Admin.AdminUsersFragment;
import com.example.argapp.R;

import java.util.List;

public class AdminUsersAdapter extends RecyclerView.Adapter<AdminUsersAdapter.ViewHolder> {

    private List<AdminUsersFragment.UserWithId> usersList;
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onViewUserDetails(AdminUsersFragment.UserWithId userWithId);
        void onDeleteUser(AdminUsersFragment.UserWithId userWithId);
    }

    public AdminUsersAdapter(List<AdminUsersFragment.UserWithId> usersList, OnUserActionListener listener) {
        this.usersList = usersList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminUsersFragment.UserWithId userWithId = usersList.get(position);

        holder.tvUserId.setText("ID: " + userWithId.userId);
        holder.tvUserName.setText(userWithId.user.getFirstName() + " " + userWithId.user.getLastName());
        holder.tvUserEmail.setText("Email: " + userWithId.user.getEmail());
        holder.tvUserPhone.setText("Phone: " + userWithId.user.getPhoneNumber());
        holder.tvUserAddress.setText("Address: " + (userWithId.user.getAddress() != null ? userWithId.user.getAddress() : "Chưa có"));

        holder.btnViewDetails.setOnClickListener(v -> listener.onViewUserDetails(userWithId));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteUser(userWithId));
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserId, tvUserName, tvUserEmail, tvUserPhone, tvUserAddress;
        Button btnViewDetails, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserId = itemView.findViewById(R.id.tv_user_id);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvUserEmail = itemView.findViewById(R.id.tv_user_email);
            tvUserPhone = itemView.findViewById(R.id.tv_user_phone);
            tvUserAddress = itemView.findViewById(R.id.tv_user_address);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}