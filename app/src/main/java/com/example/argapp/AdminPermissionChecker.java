package com.example.argapp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminPermissionChecker {

    private static final String TAG = "AdminPermissionChecker";

    public interface AdminCheckCallback {
        void onAdminCheckResult(boolean isAdmin);
    }

    public static void checkAdminPermission(Context context, AdminCheckCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            callback.onAdminCheckResult(false);
            return;
        }

        // Check if user is in admin list
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("Data/Admins");

        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isAdmin = false;

                // Check by email
                String userEmail = currentUser.getEmail();
                if (userEmail != null) {
                    for (DataSnapshot adminSnapshot : snapshot.getChildren()) {
                        String adminEmail = adminSnapshot.child("email").getValue(String.class);
                        String adminUid = adminSnapshot.child("uid").getValue(String.class);

                        if ((adminEmail != null && adminEmail.equals(userEmail)) ||
                                (adminUid != null && adminUid.equals(currentUser.getUid()))) {
                            isAdmin = true;
                            break;
                        }
                    }
                }

                Log.d(TAG, "Admin check result for " + userEmail + ": " + isAdmin);
                callback.onAdminCheckResult(isAdmin);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error checking admin permission: " + error.getMessage());
                callback.onAdminCheckResult(false);
            }
        });
    }

    public static void setupAdminData() {
        // Setup default admin accounts
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("Data/Admins");

        // Add default admin (you can change this email)
        String adminEmail = "admin@agrapp.com";
        String adminUid = "admin_uid"; // You can set a specific UID

        AdminData admin = new AdminData(adminEmail, adminUid, "Admin", "System");
        adminRef.child("admin1").setValue(admin);
    }

    public static class AdminData {
        public String email;
        public String uid;
        public String firstName;
        public String lastName;

        public AdminData() {}

        public AdminData(String email, String uid, String firstName, String lastName) {
            this.email = email;
            this.uid = uid;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }
}