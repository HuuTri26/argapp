package com.example.argapp.Utils;

import android.content.Context;
import android.content.Intent;

import com.example.argapp.Activities.AdminActivity;
import com.example.argapp.Classes.User;

public class AdminUtils {

    public static boolean isAdmin(User user) {
        return user != null && "admin".equals(user.getRole());
    }

    public static void navigateToAdminPanel(Context context) {
        Intent intent = new Intent(context, AdminActivity.class);
        context.startActivity(intent);
    }
}